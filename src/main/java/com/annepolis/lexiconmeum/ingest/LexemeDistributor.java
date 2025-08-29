package com.annepolis.lexiconmeum.ingest;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LexemeDistributor implements IngestLexemeUseCase {

    private final List<LexemeSink> sinks;

    public LexemeDistributor(List<LexemeSink> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void ingest(Lexeme lexeme) {
        for (LexemeSink sink : sinks) {
            sink.accept(lexeme);
        }
    }
}
