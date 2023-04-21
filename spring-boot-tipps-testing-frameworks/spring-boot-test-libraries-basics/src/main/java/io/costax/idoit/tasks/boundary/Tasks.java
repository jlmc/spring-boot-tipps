package io.costax.idoit.tasks.boundary;

import io.costax.idoit.tasks.control.TaskRepository;
import io.costax.idoit.tasks.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class Tasks {

    private final TaskRepository repository;

    @Autowired
    public Tasks(final TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Task create(Task task) {
        final Task saved = repository.save(task);
        return saved;
    }

    @Transactional
    public void remove(final Long id) {
        repository.removeById(id);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> getPendingTasks() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Optional<Task> getById(final Long id) {
        return repository.findById(id);
    }
}
