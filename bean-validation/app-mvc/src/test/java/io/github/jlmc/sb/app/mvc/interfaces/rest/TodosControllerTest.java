package io.github.jlmc.sb.app.mvc.interfaces.rest;

import io.github.jlmc.sb.validation.ValidationsAutoConfiguration;
import io.github.jlmc.sb.validation.advices.ConstraintViolationExceptionHandler;
import io.github.jlmc.sb.validation.advices.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.sb.validation.advices.WebExchangeBindExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(
        classes = {
                TodosController.class,
                ControllerExceptionAdvices.class,
                ValidationsAutoConfiguration.class,
                ConstraintViolationExceptionHandler.class,
                MethodArgumentNotValidExceptionHandler.class,
                WebExchangeBindExceptionHandler.class
        }
)
class TodosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void getPageSuccessful() throws Exception {
        mockMvc.perform(
                       get("/todos")
                               .queryParam("page", "2")
                               .queryParam("page_size", "3"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().json("""
                       [
                         {
                           "title": "todo-1",
                           "description": "description-1",
                           "issue_priority": 1
                         },
                         {
                           "title": "todo-2",
                           "description": "description-2",
                           "issue_priority": 1
                         }
                       ]
                       """));
    }

    @Test
    void getPageBadRequest() throws Exception {
        mockMvc.perform(
                       get("/todos")
                               .queryParam("page", "-1")
                               .queryParam("page_size", "51"))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().json("""
                       {
                         "http_code": 400,
                         "code": "X400",
                         "message": "Request cannot be processed due to validation errors",
                         "fields": [
                           {
                             "name": "page_size",
                             "description": "The Query parameter 'page_size' must be less than or equal to 50"
                           },
                           {
                             "name": "page",
                             "description": "The Query parameter 'page' must be greater than 0"
                           }
                         ]
                       }
                       """));
    }

    @Test
    void getByIdSuccessful() throws Exception {
        mockMvc.perform(get("/todos/{id}", "12345"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().json("""
                       {
                         "id": 12345,
                         "title": "todo-1",
                         "description": "description-1",
                         "issue_priority": 1
                       }
                       """)
               );
    }

    @Test
    void getByIdBadRequest() throws Exception {
        mockMvc.perform(
                       get("/todos/{id}", "123456"))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().json("""
                       {
                         "http_code": 400,
                         "code": "X400",
                         "message": "Request cannot be processed due to validation errors",
                         "fields": [
                           {
                             "name": "todo_id",
                             "description": "The Path parameter 'todo_id' must match \\"^\\\\d{1,5}$\\""
                           }
                         ]
                       }
                       """)
               );
    }

    @Test
    void postSuccessful() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:/payloads/requests/valid-todo.json");
        byte[] payload = resource.getInputStream().readAllBytes();

        mockMvc.perform(
                       post("/todos")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(payload))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().json(new String(payload)));
    }


    @Test
    void postBadRequest() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:/payloads/requests/invalid-todo.json");

        mockMvc.perform(
                       post("/todos")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(resource.getInputStream().readAllBytes()))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().json("""
                       {
                         "http_code": 400,
                         "code": "X400",
                         "message": "Request cannot be processed due to validation errors",
                         "fields": [
                           {
                             "name": "issue_priority",
                             "description": "must be less than or equal to 5"
                           },
                           {
                             "name": "title",
                             "description": "must not be blank"
                           }
                         ]
                       }
                       """)
               );
    }

}
