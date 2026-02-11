package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.function.Consumer;

public interface WiktionaryStagingService {
    void stageLexeme(Lexeme lexeme);

    void stageLinkableData(LinkableData linkableData);

    DataLinkingService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer);
}
