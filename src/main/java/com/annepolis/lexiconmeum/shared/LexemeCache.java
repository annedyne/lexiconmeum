package com.annepolis.lexiconmeum.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
class LexemeCache implements LexemeSink {

    static final Logger LOGGER = LogManager.getLogger(LexemeCache.class);

    private final HashMap<String, Lexeme> lemmas = new HashMap<>();

    Lexeme getLexeme(String key){
        return lemmas.get(key);
    }

    void addLexeme(Lexeme lexeme){
        String key = lexeme.getLemma();
        if(lemmas.containsKey(key)){
            LOGGER.info(String.format("there are two versions of %s", key)  );
        }
        lemmas.put(lexeme.getLemma(), lexeme);
    }

    @Override
    public void accept(Lexeme lexeme) {
        addLexeme(lexeme);
    }
}
