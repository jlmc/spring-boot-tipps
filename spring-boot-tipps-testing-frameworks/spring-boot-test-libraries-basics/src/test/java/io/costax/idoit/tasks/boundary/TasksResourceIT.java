package io.costax.idoit.tasks.boundary;

import io.costax.idoit.ExceptionHandlerMapper;
import io.costax.idoit.tasks.control.TaskRepository;
import io.costax.idoit.tasks.entity.Task;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TasksResourceIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @Before
    public void start() {
        final String title = "call the boss";
        task = new Task(title);
        taskRepository.save(task);
    }

    @After
    public void end() {
        taskRepository.deleteAll();
    }

    @Test
    public void shouldReturnTasksPage() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/tasks/",
                HttpMethod.GET,
                null,
                String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnPageOfTask() {
        ResponseEntity<String> response =
                testRestTemplate.exchange(
                        "/tasks",
                        HttpMethod.GET,
                        null,
                        String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        Assert.assertEquals(response.getHeaders().getContentType(), MediaType.parseMediaType("application/json;charset=UTF-8"));

        Assert.assertThat(response.getBody(), Matchers.containsString("{"));
    }

    /*
    @Test
    public void shouldGetAllPage() {
        ParameterizedTypeReference<Page<Tasks>> returnType = new ParameterizedTypeReference<Page<Tasks>>() {
        };

        final ResponseEntity<Page<Tasks>> response = testRestTemplate.exchange("/tasks", HttpMethod.GET, null, returnType);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().getContentType().equals(MediaType.parseMediaType("application/json;charset=UTF-8")));
        Assert.assertEquals(1, response.getBody().getContent().size());
        Assert.assertEquals(task, response.getBody().getContent().get(0));
    }*/

    @Test
    public void shouldGetTask() {
        ResponseEntity<Task> response =
                testRestTemplate.exchange("/tasks/{id}",
                        HttpMethod.GET,
                        null,
                        Task.class,
                        task.getId());

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(response.getHeaders().getContentType(), MediaType.parseMediaType("application/json;charset=UTF-8"));
        Assert.assertThat(response.getBody(), Matchers.notNullValue());
        Assert.assertEquals(task.getId(), response.getBody().getId());
    }

    @Test
    public void shouldNotFoundTask() {
        ResponseEntity<Task> response =
                testRestTemplate.exchange(
                        "/tasks/{id}",
                        HttpMethod.GET,
                        null,
                        Task.class, 100);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void shouldNotCreateTaskWithoutTitle() {
        Task tk = new Task("");
        HttpEntity<Task> httpEntity = new HttpEntity<>(tk);

        ResponseEntity<List<ExceptionHandlerMapper.Error>> response =
                testRestTemplate.exchange(
                        "/tasks",
                        HttpMethod.POST,
                        httpEntity
                        , new ParameterizedTypeReference<List<ExceptionHandlerMapper.Error>>() {
                        });

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertThat(response.getBody(), Matchers.not(Matchers.empty()));
        Assert.assertThat(response.getBody(), Matchers.hasSize(1));
        Assert.assertThat(response.getBody(), Matchers.contains(Matchers.hasProperty("userMessage", Matchers.is("Title should not be blank"))));
    }

    @Test
    public void shouldCreateTask() {
        Task tk = new Task("abcder-1234");
        HttpEntity<Task> httpEntity = new HttpEntity<>(tk);
        ResponseEntity<Task> response =
                testRestTemplate.exchange(
                        "/tasks",
                        HttpMethod.POST,
                        httpEntity,
                        Task.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Task result = response.getBody();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getId());
        Assert.assertEquals(tk.getTitle(), result.getTitle());
        taskRepository.deleteAll();
    }

}