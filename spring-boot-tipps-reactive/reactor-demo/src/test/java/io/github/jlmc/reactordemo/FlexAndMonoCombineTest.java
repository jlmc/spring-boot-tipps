package io.github.jlmc.reactordemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FlexAndMonoCombineTest {

    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A");
        Flux<String> flux2 = Flux.just("Z");
        Flux<String> flux3 = Flux.just("B", "C", "D");

        Flux<String> merged =
                Flux.merge(flux1, flux2, flux3)
                    .log()
                    .sort()
                    .log();

        StepVerifier.create(merged).expectSubscription()
                    .expectNext("A", "B", "C", "D", "Z")
                    .verifyComplete();
    }

    @Test
    void combineUsingMerge_withDelay() {
        Flux<String> flux1 = Flux.just("A").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("Z").delayElements(Duration.ofSeconds(1));
        Flux<String> flux3 = Flux.just("B", "C", "D").delayElements(Duration.ofSeconds(1));

        Flux<String> merged =
                Flux.merge(flux1, flux2, flux3)
                    .log()
                    //.sort()
                    .log();

        // Using The merge the final order is nondeterministic

        StepVerifier.create(merged)
                    .expectSubscription()
                    //.expectNext("A", "B", "C", "D", "Z")
                    .expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    void combineUsingConcat_withDelay() {
        Flux<String> flux1 = Flux.just("A").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("Z").delayElements(Duration.ofSeconds(1));
        Flux<String> flux3 = Flux.just("B", "C", "D").delayElements(Duration.ofSeconds(1));

        Flux<String> merged =
                Flux.concat(flux1, flux2, flux3)
                    //.log()
                    //.sort()
                    .log();

        // Using The concat the final order is deterministic

        StepVerifier.create(merged)
                    .expectSubscription()
                    .expectNext("A", "Z", "B", "C", "D")
                    //.expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    void combineUsingZip_withDelay() {
        Flux<String> flux1 = Flux.just("A").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("B", "C", "D").delayElements(Duration.ofSeconds(1));

        Flux<String> merged =
                Flux.zip(flux1, flux2, String::concat)
                    //.log()
                    //.sort()
                    .log();

        // Using The concat the final order is deterministic

        StepVerifier.create(merged)
                    .expectSubscription()
                    .expectNext("AB")
                    //.expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    void componinations() {

        var fuxo =
                executeSearch()
                        .zipWhen(this::fetchDependencies)
                        .map(it -> {

                            EventsPage t1 = it.getT1();
                            Tuple2<Map<String, Department>, Map<String, Provider>> maps = it.getT2();
                            Map<String, Department> deps = maps.getT1();
                            Map<String, Provider> providers = maps.getT2();

                            return t1.events.stream()
                                            .map(event -> new EventSummary(
                                                    event.id,
                                                    event.title,
                                                    Optional.ofNullable(deps.get(event.departmentId)).map(d -> d.name).orElse(""),
                                                    Optional.ofNullable(providers.get(event.providerId)).map(d -> d.name).orElse("")
                                            )).collect(Collectors.toList());

                        });

        // List<EventSummary> block = fuxo.block();

        //System.out.println(block);

        StepVerifier.create(fuxo.log())
                    .expectSubscription()
                    .assertNext(result -> {
                        System.out.println(result);
                    })
                    .verifyComplete();

    }

    private Mono<Tuple2<Map<String, Department>, Map<String, Provider>>> fetchDependencies(EventsPage eventsPage) {

        Set<String> departmentIds = eventsPage.events.stream().map(it -> it.departmentId).collect(Collectors.toSet());
        Set<String> providerIds = eventsPage.events.stream().map(it -> it.providerId).collect(Collectors.toSet());

        var deps =
                Flux.fromIterable(departmentIds)
                    .flatMap(this::fetchDepartment)
                    .collectMap(it -> it.id);


        var provs =
                Flux.fromIterable(providerIds)
                    .flatMap(this::fetchProvider)
                    .collectMap(it -> it.id);

        return Mono.zip(deps, provs);
    }

    public Mono<Provider> fetchProvider(String id) {
        Provider department = new Provider(id, "This a the Provider " + id);

        return Mono.just(department).delayElement(Duration.ofSeconds(5));

        /*
        return Mono.fromCallable(() -> {


            System.out.println(Thread.currentThread().getId() + " Fetching the provider " + id);
            Thread.sleep(1000);
            Provider provider = new Provider(id, "This a the proider " + id);

            System.out.println(Thread.currentThread().getId() + " Fetched the provider " + id);

            return provider;
        });
         */
    }

    public Mono<Department> fetchDepartment(String id) {
        Department department = new Department(id, "This a the Department " + id);

        return Mono.just(department).delayElement(Duration.ofSeconds(1));

        /*
        return Mono.fromCallable(() -> {


            System.out.println(Thread.currentThread().getId() + "  Fetching the Department " + id);
            Thread.sleep(1000);
            Department provider = ;

            System.out.println(Thread.currentThread().getId() + " Fetched the Department " + id);

            return provider;
        });

         */
    }


    public Mono<EventsPage> executeSearch() {

        EventsPage eventsPage1 = new EventsPage(

                List.of(
                        new Event("1", "event 1", "p1", "d1"),
                        new Event("2", "event 2", "p1", "d1"),
                        new Event("3", "event 3", "p2", "d2"),
                        new Event("4", "event 4", "p2", "d3"),
                        new Event("5", "event 4", "p2", "d4")
                )

        );

        return Mono.just(eventsPage1).log().delayElement(Duration.ofSeconds(1));

    }

    static class Department {
        final String id;
        final String name;

        Department(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Provider {
        final String id;
        final String name;

        Provider(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class EventsPage {
        final int total;
        final List<Event> events;

        EventsPage(List<Event> events) {
            this.events = events;
            this.total = events.size();
        }
    }

    static class Event {
        final String id;
        final String title;
        final String providerId;
        final String departmentId;

        public Event(String id, String title, String providerId, String departmentId) {
            this.id = id;
            this.title = title;
            this.providerId = providerId;
            this.departmentId = departmentId;
        }
    }

    static class EventSummary {
        final String id;
        final String title;
        final String departmentName;
        final String providerName;

        public EventSummary(String id, String title, String departmentName, String providerName) {
            this.id = id;
            this.title = title;
            this.departmentName = departmentName;
            this.providerName = providerName;
        }

        @Override
        public String toString() {
            return "EventSummary{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", departmentName='" + departmentName + '\'' +
                    ", providerName='" + providerName + '\'' +
                    '}';
        }
    }
}
