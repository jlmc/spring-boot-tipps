package io.github.jlmc.springqualifiers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringQualifiersApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringQualifiersApplication.class, args);
    }

}
