package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.function.Consumer;

public interface WiktionaryStagingService {
    void stageLexeme(Lexeme lexeme);

    void stageParticiple(StagedParticipleData participleData);

    ParticipleResolutionService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer);
}
