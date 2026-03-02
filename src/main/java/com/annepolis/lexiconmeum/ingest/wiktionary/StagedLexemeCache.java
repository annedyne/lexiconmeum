package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.shared.util.Utilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores Lexemes during ingest that require additional processing
 */
@Component
public class StagedLexemeCache {

    static final Logger logger = LogManager.getLogger(StagedLexemeCache.class);

    private final Map<String, List<Lexeme>> lemmaToLexemesLookup = new ConcurrentHashMap<>();

    // NEW: Compound index by lemma + POS for faster filtering
    private final Map<String, List<Lexeme>> lemmaAndPosToLexemesLookup = new ConcurrentHashMap<>();

    private final Map<String, Lexeme> gerundiveFormToLexemeLookup = new ConcurrentHashMap<>();

    private final String gerundiveKey = InflectionKey.buildParticipleSetKey(GrammaticalVoice.PASSIVE, GrammaticalTense.FUTURE);

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

        putGerundiveEntry(lexeme);
    }

    /**
     * Replace an existing lexeme with an updated version.
     * Updates all indexes appropriately.
     */
    public void replaceLexeme(Lexeme oldLexeme, Lexeme newLexeme) {
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

        putGerundiveEntry(newLexeme);

        logger.debug("Replaced lexeme in all indexes: {}", newLexeme);
    }

    private void putGerundiveEntry(Lexeme lexeme){

        if(lexeme.getInflectionIndex().containsKey(gerundiveKey)) {
            String gerundiveForm = lexeme.getInflectionIndex().get(gerundiveKey).getForm();
            // forms unlike 'lemmas' have macrons so normalize
            gerundiveFormToLexemeLookup.put(normalizeDiacritics(gerundiveForm), lexeme);
        }
    }

    public List<Lexeme> getLexemesByLemmaAndPos(String lemma, PartOfSpeech partOfSpeech) {
        String compoundKey = buildCompoundKey(lemma, partOfSpeech);
        List<Lexeme> lexemes = lemmaAndPosToLexemesLookup.get(compoundKey);
        if (lexemes != null) {
            return List.copyOf(lexemes);
        } else {
            if (partOfSpeech == PartOfSpeech.VERB) return getLexemeByGerundive(lemma);
            return List.of();
        }
    }

    private String buildCompoundKey(String lemma, PartOfSpeech pos) {
        return lemma + "|" + pos.name();
    }

    public List<Lexeme> getLexemeByGerundive(String gerundive){
        Lexeme lexeme = gerundiveFormToLexemeLookup.get(gerundive);
        return lexeme != null ? List.of(lexeme) : List.of();
    }

    public boolean containsKey(String key){
        return lemmaToLexemesLookup.containsKey(key)
        || gerundiveFormToLexemeLookup.containsKey(key)
        || lemmaAndPosToLexemesLookup.containsKey(key);
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
        return lemmaToLexemesLookup.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    void clearStaged(){
        lemmaToLexemesLookup.clear();
        gerundiveFormToLexemeLookup.clear();
    }

    protected static String normalizeDiacritics(String lemma){
        return Utilities.normalizeDiacritics(lemma);
    }

}
