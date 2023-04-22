package io.costax.idoit.tasks.control;

import io.costax.idoit.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    void removeById(Long id);

    //@Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    Task findByTitleIgnoreCase(String title);
}
