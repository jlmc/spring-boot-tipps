package io.github.jlmc.poc.adapters.ordersid.local;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderIdCreatorLocalTest {

    public static final int NUMBER_OF_EXECUTIONS = 500;
    OrderIdCreatorLocal sut = new OrderIdCreatorLocal();

    @Test
    void when_generate_id_is_executed_multiples_times__it_nevers_repetes_a_already_generated_result() {
        LinkedHashMap<OrderId, Long> ids = IntStream.range(0, NUMBER_OF_EXECUTIONS)
                .mapToObj(it -> sut.generateOrderId())
                .collect(groupByOrderIdCounting());

        assertEquals(NUMBER_OF_EXECUTIONS, ids.size());
        assertEquals(1L, ids.pollFirstEntry().getValue());
        assertEquals(1L, ids.pollLastEntry().getValue());
    }

    private static Collector<OrderId, Object, LinkedHashMap<OrderId, Long>> groupByOrderIdCounting() {
        return Collectors.collectingAndThen(
                Collectors.groupingBy(Function.identity(), Collectors.counting()),
                OrderIdCreatorLocalTest::orderByValue
        );
    }

    private static LinkedHashMap<OrderId, Long> orderByValue(Map<OrderId, Long> wordCountMap) {
        return wordCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Sorting by count value in descending order
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, // Merge function (not required in this case)
                        LinkedHashMap::new // Maintain insertion order after sorting
                ));
    }
}