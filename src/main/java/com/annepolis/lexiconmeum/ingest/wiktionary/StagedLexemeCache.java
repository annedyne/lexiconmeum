package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class StagedLexemeCache {

    static final Logger logger = LogManager.getLogger(StagedLexemeCache.class);

    private final HashMap<UUID, Lexeme> lexemeIdToLexemeLookup = new HashMap<>();

    private final Map<String, List<Lexeme>> lemmaToLexemesLookup = new ConcurrentHashMap<>();

    // NEW: Compound index by lemma + POS for faster filtering
    private final Map<String, List<Lexeme>> lemmaAndPosToLexemesLookup = new ConcurrentHashMap<>();


    public void putLexeme(Lexeme lexeme){
        if(logger.isDebugEnabled()) {
            logger.trace("accepting lexeme: {}", lexeme);
        }

        lemmaToLexemesLookup
                .computeIfAbsent(lexeme.getLemma(), k -> new CopyOnWriteArrayList<>())
                .add(lexeme);

        // Add to compound lemma+POS index
        String compoundKey = buildCompoundKey(lexeme.getLemma(), lexeme.getPartOfSpeech());

        lemmaAndPosToLexemesLookup
                .computeIfAbsent(compoundKey, k -> new CopyOnWriteArrayList<>())
                .add(lexeme);
    }

    /**
     * Replace an existing lexeme with an updated version.
     * Updates all indexes appropriately.
     */
    private void replaceLexeme(Lexeme oldLexeme, Lexeme newLexeme) {
        if (!oldLexeme.equals(newLexeme)) {
            throw new IllegalArgumentException(
                    "Cannot replace lexeme with different identity: " + oldLexeme + " vs " + newLexeme
            );
        }
        // Update lemma index - remove old, add new
        List<Lexeme> lemmaList = lemmaToLexemesLookup.get(newLexeme.getLemma());
        if (lemmaList != null) {
            lemmaList.remove(oldLexeme);
            lemmaList.add(newLexeme);
        }

        // Update compound index - remove old, add new
        String compoundKey = buildCompoundKey(newLexeme.getLemma(), newLexeme.getPartOfSpeech());
        List<Lexeme> compoundList = lemmaAndPosToLexemesLookup.get(compoundKey);
        if (compoundList != null) {
            compoundList.remove(oldLexeme);
            compoundList.add(newLexeme);
        }

        logger.debug("Replaced lexeme in all indexes: {}", newLexeme);
    }

    public List<Lexeme> getLexemesByLemma(String lemma) {
        List<Lexeme> lexemes = lemmaToLexemesLookup.get(lemma);
        return lexemes != null ? List.copyOf(lexemes) : List.of();
    }

    public List<Lexeme> getLexemesByLemmaAndPos(String lemma, PartOfSpeech partOfSpeech) {
        String compoundKey = buildCompoundKey(lemma, partOfSpeech);
        List<Lexeme> lexemes = lemmaAndPosToLexemesLookup.get(compoundKey);
        return lexemes != null ? List.copyOf(lexemes) : List.of();
    }

    private String buildCompoundKey(String lemma, PartOfSpeech pos) {
        return lemma + "|" + pos.name();
    }

    public List<Lexeme> getStagedLexemes(){
        return lemmaAndPosToLexemesLookup.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    /**
     * Get count of staged participles (for monitoring during ingestion)
     */
    public int getStagedCount() {
        return lemmaAndPosToLexemesLookup.values().stream()
                .mapToInt(List::size)
                .sum();
    }

}
