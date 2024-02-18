package io.github.jlmc.uof;

import io.github.jlmc.uof.configurations.feign.MarketplaceConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MarketplaceConfigurationProperties.class)

//@org.springframework.data.web.config.EnableSpringDataWebSupport
public class UsingOpenFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsingOpenFeignApplication.class, args);
	}

}
