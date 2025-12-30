package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
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

    private final Map<String, Lexeme> gerundiveFormToLexemeLookup = new ConcurrentHashMap<>();

    public void putLexeme(Lexeme lexeme){
        if(logger.isDebugEnabled()) {
            logger.trace("accepting lexeme: {}", lexeme);
        }

        lemmaToLexemesLookup
                .computeIfAbsent(lexeme.getLemma(), k -> new CopyOnWriteArrayList<>())
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

        putGerundiveEntry(newLexeme);

        logger.debug("Replaced lexeme in all indexes: {}", newLexeme);
    }

    private void putGerundiveEntry(Lexeme lexeme){
        String gerundiveKey = InflectionKey.buildParticipleSetKey(GrammaticalVoice.PASSIVE, GrammaticalTense.FUTURE);
        if(lexeme.getInflectionIndex().containsKey(gerundiveKey)) {
            String gerundiveForm = lexeme.getInflectionIndex().get(gerundiveKey).getForm();
            gerundiveFormToLexemeLookup.put(gerundiveForm, lexeme);
        }
    }

    public List<Lexeme> getLexemesByLemma(String lemma) {
        List<Lexeme> lexemes = lemmaToLexemesLookup.get(lemma);
        return lexemes != null ? List.copyOf(lexemes) : getLexemeByGerundive(lemma);
    }

    public List<Lexeme> getLexemeByGerundive(String gerundive){
        Lexeme lexeme = gerundiveFormToLexemeLookup.get(gerundive);
        return lexeme != null ? List.of(lexeme) : List.of();
    }

    public boolean containsKey(String key){
        return lemmaToLexemesLookup.containsKey(key)
        || gerundiveFormToLexemeLookup.containsKey(key);

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

}
