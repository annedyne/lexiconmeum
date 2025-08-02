package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.exception.LexemeTypeMismatchException;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
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
    public Optional<Lexeme> getLexemeIfPresent(UUID lexemeId) {
        return Optional.ofNullable(lexemeIdToLexemeLookup.get(lexemeId));
    }

    @Override
    public Lexeme getLexemeOfType(UUID lexemeId, GrammaticalPosition expectedType) {
        Lexeme lexeme = lexemeIdToLexemeLookup.get(lexemeId);

        boolean matches = expectedType.equals(lexeme.getPosition());
        if (!matches) {
            throw new LexemeTypeMismatchException("Expected lexeme of type " + expectedType.name());
        }

        return lexeme;
    }



    void addLexeme(Lexeme lexeme){
        if(logger.isDebugEnabled()) {
            logger.trace("accepting lexeme: {}", lexeme);
        }
        if(lexemeIdToLexemeLookup.containsKey(lexeme.getId())){
            logger.trace( "there are two versions of {} ", lexeme.getId());
        }
        lexemeIdToLexemeLookup.put(lexeme.getId(), lexeme);
    }

    @Override
    public void accept(Lexeme lexeme) {
        addLexeme(lexeme);
    }
}
