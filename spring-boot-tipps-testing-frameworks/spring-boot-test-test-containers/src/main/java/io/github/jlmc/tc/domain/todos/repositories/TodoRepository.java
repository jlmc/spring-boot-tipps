package io.github.jlmc.tc.domain.todos.repositories;

import io.github.jlmc.tc.domain.todos.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
