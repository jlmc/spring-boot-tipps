package io.github.jlmc.poc.st;

import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class ResultLoggerExtension implements TestWatcher,
        AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultLoggerExtension.class);

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
            Arrays.stream(ResultLoggerExtension.class.getCanonicalName().split("\\.")).toArray());

    private static final String RESULT_LOGGER_ON_AFTER_INSTANT_CONTEXT = "RESULT_LOGGER_ON_AFTER_INSTANT_CONTEXT";


    @Override
    public void afterAll(ExtensionContext extensionContext) {
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        ExtensionContext.Store store = context.getStore(NAMESPACE);
        Instant onBefore = getNow();
        store.put(RESULT_LOGGER_ON_AFTER_INSTANT_CONTEXT, onBefore);

        String name = context.getDisplayName().replace("_", " ");

        LOGGER.info("======>>> Starting Executing [{}] [{}]", name, onBefore);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        ExtensionContext.Store store = context.getStore(NAMESPACE);
        Instant onBefore = store.get(RESULT_LOGGER_ON_AFTER_INSTANT_CONTEXT, Instant.class);

        Instant onAfter = getNow();
        Duration between = Duration.between(onBefore, onAfter);

        String name = context.getDisplayName().replace("_", " ");

        LOGGER.info("<<<====== Ended Execution [{}] [{}] with duration [{}]ms", name, onAfter, between.toMillis() );
    }

    private static Instant getNow() {
        return Instant.now();
    }
}
