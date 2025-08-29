package com.annepolis.lexiconmeum.webapi.bff.lexemedetail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponseAssemblerService;
import org.springframework.stereotype.Service;

@Service
public class LexemeDetailService   {
    private final LexemeDetailResponseAssemblerService assembler;

    public LexemeDetailService(LexemeDetailResponseAssemblerService assembler){
        this.assembler = assembler;
    }

    public LexemeDetailResponse getLexemeDetail(Lexeme lexeme) {
        return assembler.assemble(lexeme);
    }
}
