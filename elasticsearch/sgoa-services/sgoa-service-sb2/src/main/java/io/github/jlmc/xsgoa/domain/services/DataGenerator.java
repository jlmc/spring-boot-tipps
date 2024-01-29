package io.github.jlmc.xsgoa.domain.services;

import io.github.jlmc.xsgoa.domain.entities.Account;
import io.github.jlmc.xsgoa.domain.entities.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Component
public class DataGenerator {

    public List<Customer> customers() {
        return LongStream.rangeClosed(1L, 150L)
                .mapToObj(this::createDummyCustomer)
                .collect(Collectors.toList());
    }

    private Customer createDummyCustomer(long i) {
        String number = (i <= 50) ? String.format("ay-%d", 1) : String.format("bx-%d", 2);

        int totalAccounts = totalAccount(i);

        List<Account> accounts =
                IntStream.rangeClosed(1, totalAccounts)
                        .mapToObj(it ->
                                Account.builder()
                                        .number("" + (it + totalAccounts))
                                        .countryCode(i % 2 == 0 ? "PT" : "US")
                                        .fullNumber("test_" + i + "_" + it)
                                        .build()
                        ).collect(Collectors.toList());

        var origin = i % 2 == 0 ? 1 : 2;
        return Customer.builder()
                .number(i)
                .origin(origin)
                .accounts(accounts)
                .build();
    }

    private int totalAccount(long i) {
        int totalAccounts = 1;
        if (i % 10 == 0) {
            totalAccounts = 3;
        } else if (i <= 10) {
            totalAccounts = 4;
        } else if (i > 50) {
            totalAccounts = 0;
        } else if (i % 2 == 0) {
            totalAccounts = 2;
        }
        return totalAccounts;
    }
}
