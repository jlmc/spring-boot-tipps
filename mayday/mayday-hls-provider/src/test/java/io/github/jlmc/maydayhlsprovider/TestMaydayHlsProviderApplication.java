package io.github.jlmc.maydayhlsprovider;

import org.springframework.boot.SpringApplication;

public class TestMaydayHlsProviderApplication {

    public static void main(String[] args) {
        SpringApplication.from(MaydayHlsProviderApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
