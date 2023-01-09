package io.github.jlmc.reactive.more;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StepVerifierWithVirtualTimeTest {

    @Spy
    OtherService otherService;

    @InjectMocks
    private CustomerService sut;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void withoutVirtualTimeTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        when(otherService.addToExtra(anyString())).thenAnswer(new CallsRealMethods() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return super.answer(invocation);
            }
        });

        StepVerifier.create(sut.changeOrderShippingAddress("jc").log())
                    .expectNext("a-jc;b-jc;c-jc")
                    .verifyComplete();

        countDownLatch.await(9, TimeUnit.SECONDS);
        verify(this.otherService).addToExtra(anyString());
    }

    @Test
    void withVirtualTimeTest() {
        StepVerifier.withVirtualTime(() -> sut.changeOrderShippingAddress("jc").log())
                    .thenAwait(Duration.ofSeconds(9))
                    .expectNext("a-jc;b-jc;c-jc")
                    .then(() -> verify(this.otherService).addToExtra(anyString()))
                    .verifyComplete();
    }
}

class CustomerService {

    private final OtherService otherService;

    CustomerService(OtherService otherService) {
        this.otherService = otherService;
    }

    public Mono<String> changeOrderShippingAddress(String txt) {
        return generateData(txt)
                .map(String::toLowerCase)
                .collect(Collectors.joining(";"))
                .doOnSuccess(it -> otherService.addToExtra(it)
                                               .doFirst(() -> System.out.println("do First " + Instant.now()))
                                               .doOnError(ex -> System.out.println("Om Error: " + ex.getMessage()))
                                               .doOnSuccess(result -> System.out.println("On Success: " + result))
                                               .subscribeOn(Schedulers.parallel())
                                               .subscribe());
    }

    public Flux<String> generateData(String txt) {
        return Flux.fromStream(Stream.of("a", "b", "c"))
                   .delayElements(Duration.ofSeconds(3))
                   .map(it -> String.format("%s-%s", it, txt));
    }
}

class OtherService {

    public Mono<String> addToExtra(String txt) {
        return Mono.just("Hello Duke")
                   .log()
                   .delayElement(Duration.ofSeconds(15))
                   .log()
                   .map(it -> it + " " + txt);
    }

}
