package io.github.jlmc.korders.processor.infrastruture.scheduling;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }

    /**
     * With this bean all events listener will be execute @Async by default
     */
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }

    /**
     * By default, Spring uses a SimpleAsyncTaskExecutor to actually run these methods asynchronously.
     * The defaults can be overridden at two levels – at the application level or at the individual method level.
     * <p>
     * 4.1. Override the Executor at the Method Level
     *
     * <pre>
     *     @Bean(name = "threadPoolTaskExecutor")
     *     public Executor threadPoolTaskExecutor() {
     *         return new ThreadPoolTaskExecutor();
     *     }
     *
     *    @Async("threadPoolTaskExecutor")
     *    public void asyncMethodWithConfiguredExecutor() {
     *     System.out.println("Execute method with configured executor - "
     *       + Thread.currentThread().getName());
     *   }
     * </pre>
     * <p>
     * <p>
     * 4.2. Override the Executor at the Application Level
     * The configuration class should implement the AsyncConfigurer interface – which will mean that it has the implement the getAsyncExecutor() method. It's here that we will return the executor for the entire application – this now becomes the default executor to run methods annotated with @Async:
     */
    @Override
    public Executor getAsyncExecutor() {
        return Executors.newFixedThreadPool(5, new ThreadFactoryBuilder().setNamePrefix("example-th-").build());
    }

    static class ThreadFactoryBuilder {
        private String namePrefix = null;
        private boolean daemon = false;
        private int priority = Thread.NORM_PRIORITY;

        private static ThreadFactory build(ThreadFactoryBuilder builder) {
            final String namePrefix = builder.namePrefix;
            final Boolean daemon = builder.daemon;
            final Integer priority = builder.priority;
            final AtomicLong count = new AtomicLong(0);

            return new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    if (namePrefix != null) {
                        thread.setName(namePrefix + "-" + count.getAndIncrement());
                    }
                    thread.setDaemon(daemon);
                    thread.setPriority(priority);
                    return thread;
                }
            };
        }

        public ThreadFactoryBuilder setNamePrefix(String namePrefix) {
            if (namePrefix == null) {
                throw new NullPointerException();
            }
            this.namePrefix = namePrefix;
            return this;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public ThreadFactoryBuilder setDaemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public ThreadFactory build() {
            return build(this);
        }
    }
}
