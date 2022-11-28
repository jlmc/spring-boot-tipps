package io.costax.examplesapi.eventsandobservers.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Please see the How To Do @Async in Spring documentation
 *
 * @see <a href="https://www.baeldung.com/spring-async#enable-async-support"> enable-async-support </a>
 */
@Configuration
@EnableAsync
public class AsynchronousSpringEventsConfig implements AsyncConfigurer {


    /**
     * With this bean all events listener will be execute @Async by default
     */
    //@Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }

    /**
     * By default, Spring uses a SimpleAsyncTaskExecutor to actually run these methods asynchronously.
     * The defaults can be overridden at two levels – at the application level or at the individual method level.
     *
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
     *
     *
     * 4.2. Override the Executor at the Application Level
     * The configuration class should implement the AsyncConfigurer interface – which will mean that it has the implement the getAsyncExecutor() method. It's here that we will return the executor for the entire application – this now becomes the default executor to run methods annotated with @Async:
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        return Executors.newFixedThreadPool(5, new CustomThreadFactoryBuilder().setNamePrefix("example-th-").build());
    }
}
