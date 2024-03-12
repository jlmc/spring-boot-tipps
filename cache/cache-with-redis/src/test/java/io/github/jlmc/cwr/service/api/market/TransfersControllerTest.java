package io.github.jlmc.cwr.service.api.market;

import io.github.jlmc.cwr.service.api.ExpectedResources;
import io.github.jlmc.cwr.service.domain.transfers.TransfersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TransfersController.class, TransfersService.class})
@Import(ExpectedResources.class)
class TransfersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ExpectedResources expectedResources;

    @Test
    void whenGetTheFirstPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/transfers")
                .queryParam("page", "0")
                .queryParam("size", "10")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResources.transfersPage0(), true)
                )
        ;
    }

    @Test
    void whenGetTheSecondPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/transfers")
                .queryParam("page", "1")
                .queryParam("size", "10")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResources.transfersPage1(), true)
                )
        ;
    }

    @Test
    void whenGetTheLastPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/transfers")
                .queryParam("page", "9")
                .queryParam("size", "10")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResources.transfersPage9(), true)
                )
        ;
    }

}