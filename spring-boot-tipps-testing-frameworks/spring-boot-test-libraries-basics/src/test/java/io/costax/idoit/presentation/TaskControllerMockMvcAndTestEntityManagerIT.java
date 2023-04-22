package io.costax.idoit.presentation;

import io.costax.idoit.tasks.entity.Task;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class TaskControllerMockMvcAndTestEntityManagerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestEntityManager testEntityManager;

    private Task doc;

    @Before
    public void start() {
        doc = new Task("jocking-abcd");
        testEntityManager.persist(doc);
    }

    @After
    public void after() {
        testEntityManager.getEntityManager().createQuery("delete from Task").executeUpdate();
    }

    @Test
    public void shouldShowAllTasks() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/views/tasks");
        final ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andDo(print());
        final int httpResponseStatus = resultActions.andReturn().getResponse().getStatus();

        StatusResultMatchers status = status();
        //resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(status.isOk());
        resultActions.andExpect(status.is(200));
        resultActions.andExpect(status.is(Matchers.is(200)));
    }

    @Test
    public void shouldCheckView() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/views/tasks"));

        resultActions.andDo(MockMvcResultHandlers.log());

        final ViewResultMatchers view = view();

        final ModelAndView resultModelAndView = resultActions.andReturn().getModelAndView();

        resultActions.andExpect(view.name("tasks/tasks"));

        resultActions.andExpect(view.name(Matchers.is("tasks/tasks")));
    }

    @Test
    public void shouldCheckModel() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/views/tasks"));
        ModelResultMatchers model = MockMvcResultMatchers.model();

        resultActions.andExpect(model.attribute("tasks", Matchers.hasSize(1)));

        resultActions.andExpect(model.attribute("tasks",
                Matchers.hasItem(Matchers.allOf(Matchers.hasProperty("id", Matchers.is(doc.getId())),
                        Matchers.hasProperty("title", Matchers.is(doc.getTitle()))))));

    }

    @Test
    public void shouldCheckDetailView() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/views/tasks/{id}", doc.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/task"))
                .andExpect(MockMvcResultMatchers.model().attribute("task", Matchers.any(Task.class)))
                .andExpect(MockMvcResultMatchers.model().attribute("task", doc))
                .andDo(print());
    }

    @Test
    public void shouldCreateNewTask() throws Exception {
        //final Task entity = new Task("created-dummy");
        //testEntityManager.persist(entity);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/views/tasks/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "created-dummy123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/views/tasks"))
                .andExpect(flash().attribute("sucessMessage", "Task added successful!!!"))
                .andDo(print());

        List<Task> resultList = testEntityManager.getEntityManager().createQuery("SELECT c FROM Task c", Task.class)
                .getResultList();

        Assert.assertThat(resultList.size(), Matchers.greaterThan(0));
    }

    @Test
    public void shouldNotCreateTaskWithEmptyTitle() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/views/tasks/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/tasks/new-task"))
                .andExpect(model().attribute("task", Matchers.any(Task.class)))
                .andExpect(model().attributeHasFieldErrors("task", "title"))
                .andExpect(model().attributeHasFieldErrorCode("task", "title", "NotBlank"))
                .andDo(print());
    }

}