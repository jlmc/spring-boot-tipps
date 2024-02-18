package io.github.jlmc.uof;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "spring.cloud.openfeign.client.config.marketplaceRequester.url=http://${test.wiremock.host}:${test.wiremock.port}/marketplace"
})
class FruitsApiIT extends AbstractIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void when_get_the_list_of_products_successful__it_return_200() throws Exception {
        mockMvc.perform(get("/api/fruits"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void when_get_the_page_of_products_successful__it_return_200() throws Exception {
        mockMvc.perform(get("/api/fruits/__page")
                        .queryParam("page", "2")
                        .queryParam("size", "3")
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content()
                                .json("""
                                        {
                                          "content": [
                                            {
                                              "id": "7",
                                              "name": "Longan",
                                              "description": "Sweet and succulent",
                                              "origin": "Southern China"
                                            },
                                            {
                                              "id": "8",
                                              "name": "Pawpaw",
                                              "description": "Tropical sweetness",
                                              "origin": "North America"
                                            },
                                            {
                                              "id": "9",
                                              "name": "Pomelo",
                                              "description": "Sweet and tangy citrus",
                                              "origin": "Southeast Asia"
                                            }
                                          ],
                                          "size": 3,
                                          "totalElements": 60,
                                          "totalPages": 20,
                                          "number": 2
                                        }
                                        """)
                );
    }

    @Test
    void when_get_a_fruit_full_detailed_successful__it_return_200() throws Exception {
        String productId = "1";
        mockMvc.perform(get("/api/fruits/{product-id}", productId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void when_get_a_fruit_full_detailed_and_the_fruit_is_not_in_the_repository__it_return_404() throws Exception {
        String productId = "999";
        mockMvc.perform(get("/api/fruits/{product-id}", productId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void when_an_external_api_call_returns_error__it_return_500() throws Exception {
        String productId = "404";
        mockMvc.perform(get("/api/fruits/{product-id}", productId))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void when_call_create_reservation_successful__it_returns_201() throws Exception {
        mockMvc.perform(
                post("/api/fruits/reservations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "productId": "1",
                                    "sellerId": "1",
                                    "priceDetailHash": "sjklasj",
                                    "clientNif": "123456789"
                                }
                                """)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }
}