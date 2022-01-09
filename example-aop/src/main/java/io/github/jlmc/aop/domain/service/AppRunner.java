package io.github.jlmc.aop.domain.service;

import io.github.jlmc.aop.domain.model.Todo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AppRunner implements CommandLineRunner {

    private final TodoService service;

    public AppRunner(TodoService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        Todo example = Todo.of(null, "See SLB");
        service.add(example);
    }
}
