package com.annepolis.lexiconmeum.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
public class LexemeCache implements LexemeSink, LexemeProvider {

    static final Logger logger = LogManager.getLogger(LexemeCache.class);

    private final HashMap<UUID, Lexeme> lexemeIdToLexemeLookup = new HashMap<>();

    @Override
    public Lexeme getLexeme(UUID lexemeId){
        return lexemeIdToLexemeLookup.get(lexemeId);
    }

    @Override
    public Optional<Lexeme> getLexemeIfPresent(UUID lexemeId) {
        return Optional.ofNullable(lexemeIdToLexemeLookup.get(lexemeId));
    }

    void addLexeme(Lexeme lexeme){
        if(logger.isDebugEnabled()) {
            logger.debug("accepting lexeme: {}", lexeme);
        }
        if(lexemeIdToLexemeLookup.containsKey(lexeme.getId())){
            logger.error( "there are two versions of {} ", lexeme.getId());
        }
        lexemeIdToLexemeLookup.put(lexeme.getId(), lexeme);
    }

    @Override
    public void accept(Lexeme lexeme) {
        addLexeme(lexeme);
    }
}
