package com.annepolis.lexiconmeum.lexeme.detail.indeclinable;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailPipeline;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class LexemeUninflectedService {

    private final LexemeProvider lexemeProvider;
    private final LexemeDetailPipeline assembler;


    public LexemeUninflectedService(LexemeProvider lexemeProvider, LexemeDetailPipeline assembler) {
        this.lexemeProvider = lexemeProvider;
        this.assembler = assembler;
    }

    public LexemeDetailResponse getLexemeDetail(UUID id) {
        Lexeme lexeme = lexemeProvider.getLexemeIfPresent(id)
                .orElseThrow(() -> new NoSuchElementException("Lexeme not found"));
        return assembler.assemble(lexeme);
    }
}   
