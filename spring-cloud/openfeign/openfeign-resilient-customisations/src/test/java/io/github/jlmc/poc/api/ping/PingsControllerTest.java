package io.github.jlmc.poc.api.ping;

import io.github.jlmc.poc.configurations.clock.FixedClockConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

@WebMvcTest(controllers = PingsController.class)
@Import(FixedClockConfiguration.class)
class PingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    public static Stream<Arguments> notGetHTTPMethods() {
        return Stream.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH)
                .map(Arguments::of);
    }

    public static Stream<Arguments> getHTTPMethods() {
        return Stream.of(HttpMethod.GET)
                .map(Arguments::of);
    }


    public static Stream<Arguments> preflightHTTPMethods() {
        return Stream.of(HttpMethod.OPTIONS)
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("getHTTPMethods")
    void when_request_get_ping__it_returns_200(HttpMethod httpMethod) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, "/api/ping"))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().string("pong - 2024-09-12T12:31:54Z[UTC]")
                );
    }

    @ParameterizedTest
    @MethodSource("preflightHTTPMethods")
    void when_request_preflight_request_ping__it_returns_200(HttpMethod httpMethod) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, "/api/ping"))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.header().string("Allow", "GET,HEAD,OPTIONS")
                        //MockMvcResultMatchers.content().string("pong - 2024-09-12T12:31:54Z[UTC]")
                );
    }

    @ParameterizedTest
    @MethodSource("notGetHTTPMethods")
    void when_request_non_get_ping__it_returns_405_method_not_allowed(HttpMethod httpMethod) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, "/api/ping"))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().is(HttpStatus.METHOD_NOT_ALLOWED.value())
                );
    }

}