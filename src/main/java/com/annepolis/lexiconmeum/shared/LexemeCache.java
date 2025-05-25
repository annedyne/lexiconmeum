package com.annepolis.lexiconmeum.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
class LexemeCache implements LexemeSink, LexemeProvider {

    static final Logger LOGGER = LogManager.getLogger(LexemeCache.class);

    private final HashMap<UUID, Lexeme> lemmas = new HashMap<>();

    @Override
    public Lexeme getLexeme(UUID key){
        return lemmas.get(key);
    }

    void addLexeme(Lexeme lexeme){
        if(lemmas.containsKey(lexeme.getId())){
            LOGGER.error( "there are two versions of {} ", lexeme.getId());
        }
        lemmas.put(lexeme.getId(), lexeme);
    }

    @Override
    public void accept(Lexeme lexeme) {
        addLexeme(lexeme);
    }
}
