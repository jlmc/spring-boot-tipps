package io.github.jlmc.tc.domain.todos.services;

import io.github.jlmc.tc.domain.todos.entities.Todo;
import io.github.jlmc.tc.domain.todos.repositories.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Optional;

@Transactional
@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public Todo add(@Valid Todo input) {
        return repository.saveAndFlush(input);
    }

    public void delete(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    public Optional<Todo> update(Long id, Todo input) {
        Todo todo = repository.findById(id).orElse(null);

        if (todo == null) return Optional.empty();

        todo.updateWith(input);

        repository.flush();

        return Optional.of(todo);
    }

}
