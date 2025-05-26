package com.annepolis.lexiconmeum.lexeme.detail;

import java.util.UUID;

public interface LexemeDetailService {

    DeclensionTableDTO getLexemeDetail(UUID lexemeId);
}
