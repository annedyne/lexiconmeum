
package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Manages staging of participles during parsing/ingestion and finalization after parsing completes.
 * Thread-safe for parallel ingestion.
 */
@Component
public class ParticipleResolutionService {
    private static final Logger logger = LoggerFactory.getLogger(ParticipleResolutionService.class);

    // Thread-safe staging map for participles waiting for their parent verbs
    private final Map<String, List<StagedParticipleData>> stagedParticiples = new ConcurrentHashMap<>();

    /**
     * Stage a participle for later attachment during finalization.
     * Thread-safe for parallel ingestion.
     */
    public void stageParticiple(StagedParticipleData participleData) {
        String parentLemma = participleData.getParentLemma();

        stagedParticiples.computeIfAbsent(parentLemma, k ->
                Collections.synchronizedList(new ArrayList<>())
        ).add(participleData);

        logger.debug("Staged participle '{}' for parent verb '{}'",
                participleData.getParticipleLemma(), parentLemma);
    }

    /**
     * Called after parsing is complete. Attaches all staged participles to their (staged) parent verbs.
     * Accepts a callback to ingest finalized lexemes.
     * Returns statistics about the finalization process.
     */
    public FinalizationReport finalizeParticiples(Consumer<Lexeme> reingestCallback,  StagedLexemeCache stagedLexemeCache) {
        logger.info("Starting participle finalization. {} parent verbs have staged participles",
                stagedParticiples.size());

        AtomicInteger verbsUpdated = new AtomicInteger(0);
        AtomicInteger participlesAttached = new AtomicInteger(0);
        AtomicInteger participlesUnresolved = new AtomicInteger(0);
        Map<String, List<String>> unresolvedDetails = new ConcurrentHashMap<>();

        // Process each verb parent associated with participles staged in parsing phase
        stagedParticiples.forEach((parentLemma, participles) -> {
            logger.debug("Processing {} participle(s) for verb '{}'", participles.size(), parentLemma);

            // attempt to retrieve staged verb lexemes matching verb lemma from verb lemma->participle index
            List<Lexeme> parentVerbs = stagedLexemeCache.getLexemesByLemmaAndPos(parentLemma, PartOfSpeech.VERB);

            if (parentVerbs.isEmpty()) {
                logger.warn("No parent verb found for {} staged participle(s) of '{}'",
                        participles.size(), parentLemma);
                participlesUnresolved.addAndGet(participles.size());
                unresolvedDetails.put(parentLemma, participles.stream()
                        .map(StagedParticipleData::getParticipleLemma)
                        .toList());
                return;
            }

            // Attach participles to matching verb(s)
            for (StagedParticipleData participleData : participles) {
                Lexeme matchingVerb = findMatchingVerb(parentVerbs, participleData);

                if (matchingVerb != null) {
                    Lexeme updatedVerb = attachParticipleToVerb(matchingVerb, participleData);

                    // Use callback instead of direct sink access
                    reingestCallback.accept(updatedVerb);

                    participlesAttached.incrementAndGet();

                    logger.trace("Attached participle '{}' ({}) to verb '{}'",
                            participleData.getParticipleLemma(),
                            participleData.getParticipleKey(),
                            matchingVerb.getLemma());
                } else {
                    logger.warn("Could not match participle '{}' to any verb with lemma '{}'",participleData.getParticipleLemma(), parentLemma);

                    participlesUnresolved.incrementAndGet();
                    unresolvedDetails.computeIfAbsent(
                            parentLemma, k -> Collections.synchronizedList(new ArrayList<>()))
                            .add(participleData.getParticipleLemma());
                }
            }
            verbsUpdated.incrementAndGet();
        });

        FinalizationReport report = new FinalizationReport(
                verbsUpdated.get(),
                participlesAttached.get(),
                participlesUnresolved.get(),
                unresolvedDetails
        );

        logger.info("Participle finalization complete: {}", report.getSummary());
        clearStaged();
        return report;
    }

    public int getStagedCount() {
        return stagedParticiples.values().stream().mapToInt(List::size).sum();
    }

    public void clearStaged() {
        stagedParticiples.clear();
    }

    private Lexeme findMatchingVerb(List<Lexeme> candidateVerbs, StagedParticipleData participleData) {
        if (candidateVerbs.size() == 1) {
            return candidateVerbs.get(0);
        }

        // Multiple verbs with same lemma - match by canonical form
        String targetCanonical = participleData.getParentLemmaWithMacrons();
        return candidateVerbs.stream()
                .filter(v -> v.getCanonicalForm().equals(targetCanonical))
                .findFirst()
                .orElse(candidateVerbs.get(0)); // Fallback to first if no exact match
    }

    private Lexeme attachParticipleToVerb(Lexeme verb, StagedParticipleData participleData) {

        LexemeBuilder builder = LexemeBuilder.fromLexeme(verb);
        VerbDetails.Builder verbDetailsBuilder = getOrCreateVerbDetailsBuilder(verb);

        VerbDetails.ParticipleSet participleSet = participleData.toParticipleSet();
        verbDetailsBuilder.addParticipleSet(participleSet);

        builder.setPartOfSpeechDetails(verbDetailsBuilder.build());

        return builder.build();
    }

    private VerbDetails.Builder getOrCreateVerbDetailsBuilder(Lexeme verb) {
        if (verb.getPartOfSpeechDetails() instanceof VerbDetails verbDetails) {

            VerbDetails.Builder vdBuilder = new VerbDetails.Builder();
            vdBuilder.setMorphologicalSubtype(verbDetails.getMorphologicalSubtype());

            verbDetails.getParticiples().values().forEach(vdBuilder::addParticipleSet);

            return vdBuilder;
        }

        // No existing details, create new
        return new VerbDetails.Builder();
    }

    public static class FinalizationReport {
        private final int verbsUpdated;
        private final int participlesAttached;
        private final int participlesUnresolved;
        private final Map<String, List<String>> unresolvedDetails;

        public FinalizationReport(int verbsUpdated, int participlesAttached,
                                  int participlesUnresolved, Map<String, List<String>> unresolvedDetails) {
            this.verbsUpdated = verbsUpdated;
            this.participlesAttached = participlesAttached;
            this.participlesUnresolved = participlesUnresolved;
            this.unresolvedDetails = unresolvedDetails;
        }

        public int getVerbsUpdated() {
            return verbsUpdated;
        }

        public int getParticipleAttached() {
            return participlesAttached;
        }

        public int getParticiplesUnresolved() {
            return participlesUnresolved;
        }

        public Map<String, List<String>> getUnresolvedDetails() {
            return unresolvedDetails;
        }

        public String getSummary() {
            return String.format("%d verbs updated, %d participles attached, %d unresolved",
                    verbsUpdated, participlesAttached, participlesUnresolved);
        }

        public boolean hasUnresolved() {
            return participlesUnresolved > 0;
        }
    }
}