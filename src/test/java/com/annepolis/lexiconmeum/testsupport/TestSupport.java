
package com.annepolis.lexiconmeum.testsupport;

import com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager;

/**
 * Central access to shared test support classes.
 *
 * JSON/Wiktionary helpers:
 * - com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager
 *
 * Fixture factories:
 * - com.annepolis.lexiconmeum.shared.model.LexemeFixtureFactory
 */
public final class TestSupport {

    private static final TestSupport INSTANCE = new TestSupport();
    private TestSupport() {}

    public static TestSupport getInstance() {
        return INSTANCE;
    }

    public JsonTestDataManager getJsonTestDataManager() {
        return JsonTestDataManager.getInstance();
    }

}

