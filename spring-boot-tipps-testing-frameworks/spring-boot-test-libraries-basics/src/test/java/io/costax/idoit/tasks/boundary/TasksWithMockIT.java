package io.costax.idoit.tasks.boundary;

import io.costax.idoit.tasks.control.TaskRepository;
import io.costax.idoit.tasks.entity.Task;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TasksWithMockIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    Tasks sut;

    @Autowired
    @MockBean
    TaskRepository taskRepository;

    @Test
    public void shouldNotCreateTaskWithBlankTitle() {
        expectedException.expect(javax.validation.ConstraintViolationException.class);
        expectedException.expectMessage("The Title should not be blank, because of reasons...");

        Mockito.when(taskRepository.save(Mockito.any(Task.class)))
                .thenThrow(new ConstraintViolationException("The Title should not be blank, because of reasons...", null));

        final Task doc = new Task();
        doc.setTitle("");

        sut.create(doc);

        Mockito.verify(this.taskRepository, Mockito.times(1)).save(Mockito.any(Task.class));
    }

    @Test
    public void shouldCreateTask() {
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).then((Answer<Task>) invocationOnMock -> {
            final Task argument = invocationOnMock.getArgument(0);
            argument.setId(99L);
            return argument;
        });

        final Task doc = new Task("Abcd");

        final Task taskSaved = sut.create(doc);

        Mockito.verify(this.taskRepository, Mockito.times(1)).save(Mockito.any(Task.class));
        assertThat(taskSaved.getId(), Matchers.is(99L));
    }


}