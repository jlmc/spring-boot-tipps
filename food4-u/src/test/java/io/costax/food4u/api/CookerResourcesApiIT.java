package io.costax.food4u.api;

import io.costax.food4u.domain.model.Cooker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static io.costax.food4u.ResourceUtils.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
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
    void should_return_status_OK_when_GET_cookers() {

        //@formatter:off
        List<Cooker> cookers = given()
             //.basePath("/cookers")
             //.port(port)
             .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.notNullValue())
            .body("", Matchers.hasSize(2))
                .extract()
                    .body()
                    .jsonPath().getList(".", Cooker.class)
                    //.body("name", hasItems("Mario Nabais"))
                    //.body("nome", hasItems("Indiana", "Tailandesa"));
        ;
        //@formatter:on

        Assert.assertThat(cookers, containsInAnyOrder(
                hasProperty("name", Matchers.is("Mario Nabais")),
                hasProperty("name", Matchers.is("Stu"))
        ));
    }

    @Test
    void should_return_status_CREATED_when_POST_cookers() {
        //@formatter:off
        Cooker cooker = given()
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
        .extract()
            .as(Cooker.class);
        //@formatter:on

        Assert.assertThat(cooker, notNullValue());
    }
}