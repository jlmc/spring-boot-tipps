package io.github.jlmc.cwr.service.api.clubs;

import io.github.jlmc.cwr.service.AbstractIT;
import io.github.jlmc.cwr.service.api.ExpectedResources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Import({ExpectedResources.class, AbstractIT.MockConfiguration.class})
class ClubsControllerIT extends AbstractIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ExpectedResources expectedResources;

    @Test
    void when_post_new_club_in_a_valid_request() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/clubs")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "GD Os Flexas"
                    }
                    """)
        ;

        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().exists("Location"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                            """
                            {"id":1,"name":"GD Os Flexas"}
                            """, true)
                )
        ;
    }

    @Test
    void when_post_new_club_in_a_invalid_request() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/clubs")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": " "
                    }
                    """)
                ;

        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType("application/problem+json"),
                        content().json(
                                """
                                {
                                  "type": "about:blank",
                                  "title": "Bad Request",
                                  "status": 400,
                                  "detail": "Invalid request content.",
                                  "instance": "/api/clubs"
                                }
                                """, true)
                )
        ;
    }
}