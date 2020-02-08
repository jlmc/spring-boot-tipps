package io.costax.food4u.api;

import io.costax.food4u.db.DataBaseCleaner;
import io.costax.food4u.domain.model.PaymentMethod;
import io.costax.food4u.domain.repository.PaymentMethodRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;


//@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-it.properties")
public class PaymentMethodResourcesApiIT {

    @Autowired
    DataBaseCleaner cleaner;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.basePath = "/payment-methods";
        RestAssured.port = port;

        cleaner.clearTables();
        prepareData();
    }


    @Test
    @DisplayName("should get the list of all payment methods")
    public void should_GET_all_Payment_Method() {
        //@formatter:off
        given()
                //.basePath("/cookers")
                //.port(port)
                .accept(ContentType.JSON)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.notNullValue())
            //    .body("", Matchers.hasSize(3))
            //.extract()
            //    .body()
            //        .jsonPath().getList(".", PaymentMethod.class)
        ;
        //@formatter:on
    }

    @Test
    void should_get_one_payment_method_by_id() {
        given()
                .pathParam("payment-method-id", 1)
                .accept(ContentType.JSON)
                .when()
                .get("/{payment-method-id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void prepareData() {
        paymentMethodRepository.save(PaymentMethod.of("VISA"));
        paymentMethodRepository.save(PaymentMethod.of("MONEY"));
        paymentMethodRepository.save(PaymentMethod.of("FAVORS"));
    }
}