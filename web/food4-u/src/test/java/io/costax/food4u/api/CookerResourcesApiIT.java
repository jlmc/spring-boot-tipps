package io.costax.food4u.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static io.costax.food4u.ResourceUtils.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("it")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CookerResourcesApiIT {


    @Autowired
    Flyway flyway;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        // the callback after migrate will be executed
        flyway.migrate();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.basePath = "/cookers";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("should return HTTP status OK (200) when GET /cookers")
    @Order(1)
    void when_get_cookers_then_should_return_ok_status_code() {
        //@formatter:off
        final Map responseBody = given()
             //.basePath("/cookers")
             //.port(port)
             .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .log().body()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.notNullValue())
            /*.body("", Matchers.hasSize(1))
            .body("$", hasItem(Map.of("id", 1, "title", "Mario Nabais")))
            .body("$", hasItem(Map.of("id", 2, "title", "Alberto Chacal")))
            .body("$", hasItem(Map.of("id", 3, "title", "Carlos Lisboa")))*/
        .extract()
             .as(Map.class);

        Assertions.assertNotNull(responseBody);
        //Assertions.assertEquals(3, responseBody.getContent().size());
        //final Map<String, Object> map = responseBody.get(0);
        //Assertions.assertNotNull(map);
        //Assertions.assertTrue(map.containsKey("id"));
        //Assertions.assertTrue(map.containsKey("title"));
    }

    @Test
    void when_get_cooker_by_id_successful_the_json_response_should_contain_the_title_property() {
        //@formatter:off
        given()
           .basePath("/cookers")
           .port(port)
           .contentType(ContentType.JSON)
           .accept(ContentType.JSON)
           .pathParam("cooker-id", 1)
        .when()
           .get("/{cooker-id}")
        .then()
           .statusCode(HttpStatus.OK.value())
           .body(Matchers.notNullValue());
           //.body(Matchers.containsString("{\"id\":1,\"title\":\"Mario Nabais\",\"_links\":{\"self\":{\"href\":\"http://localhost:52015/cookers/1\"},\"collection\":{\"href\":\"http://localhost:52015/cookers\"}}}"));
        //@formatter:on
    }

    @Test
    void when_post_cookers_then_should_return_http_status_created() {
        //@formatter:off
        given()
            .basePath("/cookers")
            .port(port)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            //.body("{ \"title\": \"Allen\" }")
            .body(getContentFromResource("/jsons/CookerResourcesApiIT-should_return_status_CREATED_when_POST_cookers.json"))
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body(Matchers.notNullValue())
            .body("title", is("Allen"));
    }
}
