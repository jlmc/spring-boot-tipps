package io.github.jlmc.aop.domain.service;

import io.github.jlmc.aop.core.log.LogExecutionTime;
import io.github.jlmc.aop.domain.model.Todo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final IdGenerator idGenerator;

    private Map<Integer, Todo> todos = new HashMap<>();

    public TodoService(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @LogExecutionTime
    public List<Todo> all() {
        return todos.values()
                    .stream()
                    .sorted(Comparator.comparingInt(Todo::getId).reversed())
                    .collect(Collectors.toList());
    }

    @LogExecutionTime
    public Todo add(Todo input) {
        Todo newOne = Todo.of(idGenerator.generate(), input.getTitle());

        todos.put(newOne.getId(), newOne);

        return newOne;
    }

    public void remove(Integer id) {
        todos.remove(id);
    }
}
