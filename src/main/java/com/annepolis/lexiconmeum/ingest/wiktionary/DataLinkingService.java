
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

        // Process each lexeme parent associated with linkables staged in the parsing phase
        stagedDataToLink.forEach((parentLemma, linkables) -> {
            logger.debug("Processing {} linkable(s) for lexeme '{}'", linkables.size(), parentLemma);

            List<Lexeme> parentLexemes = new ArrayList<>();
            if(!stagedLexemeCache.containsKey(parentLemma)){
                // Check for mapping of non-lemma linkable parents to linkable lemmas to lexeme lemmas
                List<String> parentLemmas = linkableDataLemmaToParentLemma.computeIfAbsent(parentLemma, k -> new CopyOnWriteArrayList<>());
                for(String lexemeLemma : parentLemmas){
                    // attempt to retrieve staged lexeme lexemes matching lexeme lemma from lexeme lemma->linkable index
                    parentLexemes.addAll(stagedLexemeCache.getLexemesByLemma(lexemeLemma)) ;
                }
            } else {
                // attempt to retrieve staged lexeme lexemes matching lexeme lemma from lexeme lemma->linkable index
                parentLexemes = stagedLexemeCache.getLexemesByLemma(parentLemma);

            }

            if (parentLexemes.isEmpty()) {
                logger.warn("No parent lexeme found for {} staged linkable(s) of '{}'",
                        linkables.size(), parentLemma);
                linkablesUnresolved.addAndGet(linkables.size());
                unresolvedDetails.put(parentLemma, linkables.stream()
                        .map(LinkableData::getLemma)
                        .toList());
                return;
            }

            // Attach non-lemma forms to matching lexeme(s)
            for (LinkableData dataToLink : linkables) {
                Lexeme matchingParentLexeme = findMatchingVerb(parentLexemes, dataToLink);

                if (matchingParentLexeme != null) {
                    Lexeme updatedVerb = dataToLink.link(matchingParentLexeme);

                    // Update the staged cache with the new version
                    stagedLexemeCache.replaceLexeme(matchingParentLexeme, updatedVerb);

                    // Use callback instead of direct sink access
                    reingestCallback.accept(updatedVerb);

                    linkablesAttached.incrementAndGet();

                    // refresh so the next iteration picks up any updates
                    parentLexemes = stagedLexemeCache.getLexemesByLemma(parentLemma);
                    logger.trace("Attached linkable '{}' ({}) to lexeme '{}'",
                            dataToLink.getLemma(),
                            dataToLink.getDataKey(),
                            matchingParentLexeme.getLemma());
                } else {
                    logger.warn("Could not match linkable '{}' to any lexeme with lemma '{}'",dataToLink.getLemma(), parentLemma);

                    linkablesUnresolved.incrementAndGet();
                    unresolvedDetails.computeIfAbsent(
                            parentLemma, k -> Collections.synchronizedList(new ArrayList<>()))
                            .add(dataToLink.getLemma());
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

    public int getStagedCount() {
        return stagedDataToLink.values().stream().mapToInt(List::size).sum();
    }

    public void clearStaged() {
        stagedDataToLink.clear();
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