package com.annepolis.lexiconmeum.ingest;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Distributes ingested {@link Lexeme} instances to all configured {@link LexemeSink}s.
 *
 * <p>Intended to be used as an application-level orchestrator that fans out each lexeme
 * to multiple downstream sinks (e.g., persistence, indexing, analytics). The distribution
 * is performed synchronously in the current thread in the order provided by dependency
 * injection.
 *
 * <h2>Behavior</h2>
 * <ul>
 *   <li>Calls {@link LexemeSink#accept(Lexeme)} on each sink for every ingested lexeme.</li>
 *   <li>Does not perform retries or error isolation; an exception from one sink will stop
 *       processing and propagate to the caller.</li>
 *   <li>No internal buffering or concurrency is introduced by this class.</li>
 * </ul>
 *
 * <h2>Thread-safety</h2>
 * <p>This class is stateless and thread-safe; thread-safety of the overall pipeline depends
 * on the provided sinks.</p>
 *
 * <h2>Usage</h2>
 * <p>Register one or more {@link LexemeSink} beans and inject this service to ingest
 * lexemes. Consider wrapping sinks with error handling or using an asynchronous
 * dispatcher if failures in one sink must not block others.</p>
 */
@Service
public class LexemeDistributor implements IngestLexemeUseCase {

    private final List<LexemeSink> sinks;
    
    public LexemeDistributor(List<LexemeSink> sinks) {
        this.sinks = sinks;
    }

    /**
     * Ingests a lexeme by forwarding it to all configured sinks in sequence.
     *
     * @param lexeme the lexeme to distribute; must not be {@code null}
     * @throws RuntimeException if any sink throws during processing; propagation is by design
     */
    @Override
    public void ingest(Lexeme lexeme) {
        for (LexemeSink sink : sinks) {
            sink.accept(lexeme);
        }
    }
}
