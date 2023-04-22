package io.costax.idoit.tasks.boundary;

import io.costax.idoit.tasks.control.TaskRepository;
import io.costax.idoit.tasks.entity.Task;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TasksIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    Tasks sut;
    @Autowired
    TaskRepository taskRepository;

    @Test
    public void shouldNotCreateTaskWithBlankTitle() {
        expectedException.expect(javax.validation.ConstraintViolationException.class);
        expectedException.expectMessage(Matchers.containsString("should not be blank"));

        final Task doc = new Task();
        doc.setTitle("");

        sut.create(doc);
    }

    @Test
    @Transactional
    public void shouldCreateANewTask() {
        final Task doc = new Task();
        doc.setTitle("dummy-tests");

        final Task savedDoc = sut.create(doc);

        Assert.assertThat(savedDoc, Matchers.notNullValue());
        Assert.assertThat(savedDoc.getId(), Matchers.notNullValue());

        taskRepository.removeById(savedDoc.getId());
    }

    @Test
    public void shouldRemoveTask() {
        final Task doc = new Task();
        final UUID uuid = UUID.randomUUID();
        final String title = "dummy-that-should-be-removed >> " + uuid;

        doc.setTitle(title);

        final Task savedDoc = sut.create(doc);

        Assert.assertThat(savedDoc, Matchers.notNullValue());
        Assert.assertThat(savedDoc.getId(), Matchers.notNullValue());

        sut.remove(savedDoc.getId());

        final Task byTitleRegex = taskRepository.findByTitleIgnoreCase(title);
        assertThat(byTitleRegex, Matchers.nullValue());
    }
}