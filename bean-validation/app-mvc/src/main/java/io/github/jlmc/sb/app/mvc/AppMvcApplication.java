package io.github.jlmc.sb.app.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.jlmc")
public class AppMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppMvcApplication.class, args);
    }

}
