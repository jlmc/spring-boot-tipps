package io.github.jlmc.poc.st;

import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResultLoggerExtension implements TestWatcher, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultLoggerExtension.class);

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        String name = extensionContext.getDisplayName().replace("_", " ");

        LOGGER.info("======>>> Starting Executing [{}]", name);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        String name = extensionContext.getDisplayName().replace("_", " ");

        LOGGER.info("<<<====== Ended Execution [{}]", name);
    }
}
