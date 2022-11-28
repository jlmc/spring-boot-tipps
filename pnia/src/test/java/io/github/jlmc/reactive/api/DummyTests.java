package io.github.jlmc.reactive.api;

import io.github.jlmc.reactive.domain.services.prefixes.PrefixesConfiguration;

import java.io.IOException;

public class DummyTests {

    public static void main(String[] args) throws IOException {


        var s = PrefixesConfiguration.PathType.CLASSPATH.readLines("/prefixes.txt")
                                                                      .stream()
                                                                      .map(String::length)
                                                                      .max(Integer::compareTo).orElse(0);

        System.out.println(s);


        // Enum.map(111..350, fn i -> '"+58940001#{i}"' end), ", "

        /*
        var s =
        Stream.iterate(111, i -> i <= 350, i -> i +1)
                .map(i -> "+58940001" + i)
                .map(i -> "\"" + i + "\"" )
                .collect(Collectors.joining(",", "[", "]"));


        System.out.println(s);

         */
    }



}
