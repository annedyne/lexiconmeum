package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public final class LexicalTagResolver {

    public boolean applyToLexeme(String tag, LexemeBuilder builder, Logger logger) {
        return resolve(tag)
                .map(handler -> {
                    handler.accept(builder);
                    return true;
                })
                .orElseGet(() -> {
                    if (logger != null && logger.isTraceEnabled()) {
                        logger.trace("Unknown sense tag: '{}'", tag);
                    }
                    return false;
                });
    }

    public Optional<java.util.function.Consumer<LexemeBuilder>> resolve(String tag) {
        // Try POS detail factory
        Optional<PartOfSpeechDetailFactory> posOpt = PartOfSpeechDetailFactory.fromTag(tag);
        if (posOpt.isPresent()) {
            PartOfSpeechDetailFactory f = posOpt.get();
            return Optional.of(f::applyTo);
        }

        // Try inflection class factory
        Optional<InflectionClassFactory> inflectionClassOpt = InflectionClassFactory.fromTag(tag);
        if (inflectionClassOpt.isPresent()) {
            InflectionClassFactory f = inflectionClassOpt.get();
            return Optional.of(f::applyTo);
        }

        return Optional.empty();
    }

    public void applyAllToLexeme(Iterable<String> tags, LexemeBuilder builder, Logger logger) {
        for (String t : tags) {
            applyToLexeme(t, builder, logger);
        }
    }

    // Apply a single inflection tag (e.g., person/tense/case/number/etc.)
    public boolean applyToInflection(String tag, InflectionBuilder builder, Logger logger) {
        String norm = tag == null ? "" : tag.toLowerCase();
        Optional<InflectionFeatureFactory> opt = InflectionFeatureFactory.fromTag(norm);
        if (opt.isPresent()) {
            opt.get().applyTo(builder);
            return true;
        }
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace("Unknown inflection tag: '{}'", tag);
        }
        return false;
    }

    // Apply many inflection tags
    public void applyAllToInflection(Iterable<String> tags, InflectionBuilder builder, Logger logger) {
        for (String t : tags) {
            applyToInflection(t, builder, logger);
        }
    }

    public Optional<Consumer<InflectionBuilder>> resolveInflection(String tag) {
        String norm = tag == null ? "" : tag.toLowerCase();
        return InflectionFeatureFactory.fromTag(norm).map(f -> f::applyTo);
    }
}
