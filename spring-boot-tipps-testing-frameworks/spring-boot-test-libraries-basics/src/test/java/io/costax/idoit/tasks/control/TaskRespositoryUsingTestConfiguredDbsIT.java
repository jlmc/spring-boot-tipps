package io.costax.idoit.tasks.control;

import io.costax.idoit.tasks.entity.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRespositoryUsingTestConfiguredDbsIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private TaskRepository taskRepository;

    private Task doc;

    @Before
    public void before() {
        doc = new Task();
        doc.setTitle("buy christmas gifts");
    }

    @Test
    public void shouldGetTaskById() {
        Task task = taskRepository.findById(1L).orElse(null);

        Assert.assertThat(task, nullValue());
    }

    @Test
    public void shouldNotCreateTaskWithBlankNames() {
        expectedException.expect(javax.validation.ConstraintViolationException.class);

        var t = new Task();
        t.setTitle("");

        Task task = taskRepository.save(t);

        Assert.assertThat(task, notNullValue());
    }

    @Test
    public void shouldCreateTaskWithNotBlankNames() {
        Task task = taskRepository.save(doc);

        Assert.assertThat(task, notNullValue());
    }


}


