package io.github.jlmc.uof;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestUsingOpenFeignApplication {

	public static void main(String[] args) {
		SpringApplication.from(UsingOpenFeignApplication::main).with(TestUsingOpenFeignApplication.class).run(args);
	}

}
