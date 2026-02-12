package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Manages staging and linking of non-lemma forms to Lexemes 
 * during parsing/ingestion and finalization after parsing completes.
 * Thread-safe for parallel ingestion.
 */
@Component
public class DataLinkingService {
    private static final Logger logger = LoggerFactory.getLogger(DataLinkingService.class);

    // Thread-safe staging map for non-lemma forms waiting for their parent lexemes
    private final Map<String, List<LinkableData>> stagedDataToLink = new ConcurrentHashMap<>();

    // Some 'form_of' attributes point to that non-lemma form's main form, not the parent's lemma form
    // Map them here so they can be traced to the parent lemma.
    private final Map<String, List<String>> linkableDataLemmaToParentLemma = new ConcurrentHashMap<>();

    /**
     * Stage a linkable for later attachment during finalization.
     * Thread-safe for parallel ingestion.
     */
    public void stageDataToLink(LinkableData dataToLink) {
        String parentLemma = dataToLink.getLinkingLemma();

        stagedDataToLink.computeIfAbsent(parentLemma, k ->
                Collections.synchronizedList(new ArrayList<>())
        ).add(dataToLink);

        // Sometimes the parent-lemma of a non-lemma (linkable) will not be a 'Lexeme' lemma
        // but the canonical form of another non-lemma - which in turn will point to a Lexeme lemma
        // so storing not just parentLemmas of linkables, but also their lemmas -> parentLemma mapping
        linkableDataLemmaToParentLemma.computeIfAbsent(dataToLink.getLemma(), k ->
                Collections.synchronizedList(new ArrayList<>())
        ).add(dataToLink.getLinkingLemma());
        logger.trace("Staged linkable '{}' for parent lexeme '{}'",
                dataToLink.getLemma(), parentLemma);
    }

    /**
     * Called after parsing is complete. Attaches all staged non-lemma forms to their (staged) parent lexemes.
     * Accepts a callback to ingest finalized lexemes.
     * Returns statistics about the finalization process.
     */
    public FinalizationReport finalizeParticiples(Consumer<Lexeme> reingestCallback,  StagedLexemeCache stagedLexemeCache) {
        logger.info("Starting linkable finalization. {} parent lexemes have staged linkables",
                stagedDataToLink.size());

        AtomicInteger lexemesUpdated = new AtomicInteger(0);
        AtomicInteger linkablesAttached = new AtomicInteger(0);
        AtomicInteger linkablesUnresolved = new AtomicInteger(0);
        Map<String, List<String>> unresolvedDetails = new ConcurrentHashMap<>();

        // Process each lexeme parent associated with linkables staged in the parsing phase.
        stagedDataToLink.forEach((parentLemma, linkables) -> {
            logger.debug("Processing {} linkable(s) for lexeme '{}'", linkables.size(), parentLemma);

            List<Lexeme> parentLexemes = new ArrayList<>();
            if(!stagedLexemeCache.containsKey(parentLemma)){
                // If no direct mapping to a Lexeme exists, check for a mapping to another non-lexeme
                // and get their parent-lemmas.
                List<String> parentLemmas = linkableDataLemmaToParentLemma.computeIfAbsent(parentLemma, k -> new CopyOnWriteArrayList<>());

                // Pick up all Lexemes matching the above parentLemmas.
                for(String lexemeLemma : parentLemmas){
                    parentLexemes.addAll(stagedLexemeCache.getLexemesByLemma(lexemeLemma)) ;
                }
            } else {
                // attempt to retrieve staged lexeme lexemes matching lexeme lemma from lexeme lemma->linkable index
                parentLexemes = stagedLexemeCache.getLexemesByLemma(parentLemma);

            }

            // If no matching parentLexemes exist, update the 'unresolved' stats and skip.
            if (parentLexemes.isEmpty()) {
                linkables.forEach(linkable ->
                        markUnresolved(parentLemma, linkable, linkablesUnresolved, unresolvedDetails)
                );
                return;
            }

                // Attach non-lemma forms to matching lexeme(s)
                for (LinkableData dataToLink : linkables) {
                    // Refresh parent lexemes from any changes effected in the previous iteration
                    List<Lexeme> currentParentLexemes = stagedLexemeCache.getLexemesByLemma(parentLemma);
                    
                    if (currentParentLexemes.isEmpty()) {
                        markUnresolved(parentLemma, dataToLink, linkablesUnresolved, unresolvedDetails);
                        continue;
                    }
                    
                    Lexeme matchingParentLexeme = findMatchingVerb(currentParentLexemes, dataToLink);

                    if (matchingParentLexeme != null) {
                        Lexeme updatedVerb = dataToLink.link(matchingParentLexeme);

                        // Update the staged cache with the new version
                        stagedLexemeCache.replaceLexeme(matchingParentLexeme, updatedVerb);

                        // Use callback instead of direct sink access
                        reingestCallback.accept(updatedVerb);

                        linkablesAttached.incrementAndGet();

                        logger.trace("Attached linkable '{}' ({}) to lexeme '{}'",
                                dataToLink.getLemma(),
                                dataToLink.getDataKey(),
                                matchingParentLexeme.getLemma());
                    } else {
                        markUnresolved(parentLemma, dataToLink, linkablesUnresolved, unresolvedDetails);
                    }
                }
            lexemesUpdated.incrementAndGet();
        });

        FinalizationReport report = new FinalizationReport(
                lexemesUpdated.get(),
                linkablesAttached.get(),
                linkablesUnresolved.get(),
                unresolvedDetails
        );

        logger.info("Participle finalization complete: {}", report.getSummary());
        clearStaged();
        return report;
    }

    private Lexeme findMatchingVerb(List<Lexeme> candidateLexemes, LinkableData dataToLink) {
        if (candidateLexemes.size() == 1) {
            return candidateLexemes.get(0);
        }

        // Multiple lexemes with the same lemma - match by canonical form
        String targetCanonical = dataToLink.getLinkingLemmaWithMacrons();
        return candidateLexemes.stream()
                .filter(v -> v.getCanonicalForm().equals(targetCanonical))
                .findFirst()
                .orElse(candidateLexemes.get(0)); // Fallback to first if no exact match
    }

    private static void markUnresolved(String parentLemma, LinkableData dataToLink, AtomicInteger linkablesUnresolved, Map<String, List<String>> unresolvedDetails) {
        logger.warn("Could not match linkable '{}' to any lexeme with lemma '{}'",dataToLink.getLemma(), parentLemma);

        linkablesUnresolved.incrementAndGet();
        unresolvedDetails.computeIfAbsent(
                        parentLemma, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(dataToLink.getLemma());
    }

    public int getStagedCount() {
        return stagedDataToLink.values().stream().mapToInt(List::size).sum();
    }

    public void clearStaged() {
        stagedDataToLink.clear();
    }

    public record FinalizationReport(int lexemesUpdated, int linkablesAttached, int linkablesUnresolved,
                                     Map<String, List<String>> unresolvedDetails) {

        public String getSummary() {
            return String.format("%d lexemes updated, %d linkables attached, %d unresolved",
                        lexemesUpdated, linkablesAttached, linkablesUnresolved);
        }
        public boolean hasUnresolved() {
                return linkablesUnresolved > 0;
            }
    }
}