package io.costax.food4u.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.flywaydb.core.Flyway;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.number.BigDecimalCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.costax.food4u.ResourceUtils.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestaurantResourcesApiIT {

    @Autowired
    Flyway flyway;
    @LocalServerPort
    private int port;

    public static Matcher<Object> numberCloseTo(final BigDecimal expected, final BigDecimal error) {
        return new CustomMatcher<Object>("The Value should be: " + expected) {

            @Override
            public boolean matches(final Object actual) {
                Number number = (Number) actual;
                final BigDecimal bigDecimal = BigDecimal.valueOf(number.doubleValue());

                final Matcher<BigDecimal> bigDecimalMatcher = BigDecimalCloseTo.closeTo(expected, error);
                return bigDecimalMatcher.matches(bigDecimal);
            }
        };
    }

    @BeforeEach
    void setUp() {
        // the callback after migrate will be executed
        flyway.migrate();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.basePath = "/restaurants";
        RestAssured.port = port;
    }

    @Test
    void when_get_restaurants_then_should_return_2_elements() {
        //@formatter:off
        var result = given()
                //.basePath("/cookers")
                //.port(port)
                .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .log().body()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.notNullValue())
            .body("", Matchers.hasSize(2))
        .extract()
            .body()
                .jsonPath().getList(".", HashMap.class);
        //@formatter:on

        final Map<String, Object> restaurant0 = result.get(0);
        Assertions.assertTrue(restaurant0.containsKey("address"));
        Assertions.assertTrue(restaurant0.containsKey("cooker"));
        Assertions.assertTrue(restaurant0.containsKey("name"));
        Assertions.assertTrue(restaurant0.containsKey("id"));
        Assertions.assertTrue(restaurant0.containsKey("takeAwayTax"));
        Assertions.assertFalse(restaurant0.containsKey("createdAt"));
        Assertions.assertFalse(restaurant0.containsKey("updatedAt"));
        Assertions.assertFalse(restaurant0.containsKey("paymentMethods"));
        Assertions.assertFalse(restaurant0.containsKey("version"));
        Map<String, Object> cooker = (Map<String, Object>) restaurant0.getOrDefault("cooker", Map.of());
        Assertions.assertTrue(cooker.containsKey("id"));
        Assertions.assertTrue(cooker.containsKey("title"));
        final Map<String, Object> address = (Map<String, Object>) restaurant0.getOrDefault("address", Map.of());
        Assertions.assertTrue(address.containsKey("street"));
        Assertions.assertTrue(address.containsKey("zipCode"));
        Assertions.assertTrue(address.containsKey("city"));
    }

    @Test
    void when_get_restaurant_by_id_then_should_return_the_json_object() {
        //@formatter:off
        final String result = given()
              //  .basePath("/cookers")
                .port(port)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", 1)
        .when()
                .get("/{id}")
        .then()
                .log()
                    .body()
        .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(notNullValue())
        .extract().asString();
        //@formatter:on

        Assertions.assertEquals(result, getContentFromResource("/jsons/restaurant-get-by-id-expected-result.json"));
    }

    @Test
    void when_post_restaurant_successful_then_should_return_created_status_when_the_body() {
        //@formatter:off
        final var restaurant = given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(getContentFromResource("/jsons/restaurant-post-payload.json"))
                .when()
                    .post()
                .then()
                    .log()
                        .body(false)
                .assertThat()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(notNullValue())
                    .header("Location", notNullValue())
                .extract()
                   .asString();
        //@formatter:on

        Assertions.assertNotNull(restaurant);
        Assertions.assertTrue(restaurant.startsWith("{\"id\""));
        Assertions.assertTrue(restaurant.endsWith(",\"name\":\"Casa do Rio\",\"takeAwayTax\":0.5,\"cooker\":{\"id\":1,\"title\":\"Mario Nabais\"},\"address\":{\"street\":\"Quinta St. Maria\",\"city\":\"Condeixa\",\"zipCode\":\"3030\"},\"active\":false}"));
    }

    @Test
    void when_post_restaurant_with_cooker_title_then_should_return_badrequest_status_with_the_body() {
        //@formatter:off
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(getContentFromResource("/jsons/restaurant-post-invalid-payload-with-cooker-title.json"))
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(notNullValue())
            //.body(Matchers.containsString("status\": 400"))
            .body("status", is(400))
            .body("title", is("Incomprehensible Message"))
            .body("detail", is("The property 'cooker.title' do not exists"))
            .log().body(false)
        .extract()
            .asString();
        //@formatter:on
    }

    @Test
    void when_post_restaurant_with_payments_methods_then_should_return_badrequest_status_with_the_body() {
        //@formatter:off
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(getContentFromResource("/jsons/restaurant-post-invalid-payload-with-payment-methods.json"))
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(notNullValue())
            .body("status", is(400))
            .body("title", is("Incomprehensible Message"))
            .body("detail", is("The property 'paymentMethods' do not exists"))
            .log().body(true)
        .extract()
            .asString();
        //@formatter:on
    }

    @Test
    @DisplayName("Patch restaurant resource")
    void when_patch_successful_restaurants_then_should_return_ok_status_with_the_updated_representation() {
        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", 1)
                .body(getContentFromResource("/jsons/restaurant-patch-payload.json"))
         .when()
                .patch("/{id}")
        .then()
                .log().body(true)
                .statusCode(HttpStatus.OK.value())
                .body(notNullValue())
                .body("name", is("Dona Maria1"))
                //.body("takeAwayTax", BigDecimalCloseTo.closeTo(BigDecimal.valueOf(0.54), BigDecimal.valueOf(0.00999)))
                //.body("takeAwayTax", is(0.54D))
                .body("takeAwayTax", numberCloseTo(BigDecimal.valueOf(0.54D), BigDecimal.valueOf(0.00999D)))
        .extract()
                .as(Map.class);
        //@formatter:on
    }

    @Test
    void when_delete_restaurants_id_inactivation_then_should_active_the_resource_and_return_204() {
        //@formatter:off
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .delete("/{id}/inactivation")
        .then()
            .log().headers()
        .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .body(notNullValue());
        //@formatter:on
    }

    @Test
    void when_put_restaurants_id_activation_then_should_active_the_resource_and_return_204() {
        //@formatter:off
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .pathParam("id", 1)
        .when()
            .put("/{id}/activation")
        .then()
            .log().headers()
        .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .body(notNullValue());
        //@formatter:on
    }
}