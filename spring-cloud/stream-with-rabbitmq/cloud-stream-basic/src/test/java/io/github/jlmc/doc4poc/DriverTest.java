package io.github.jlmc.doc4poc;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class DriverTest {

    @Test
    void httpStatus() {
        HttpStatus badGateway = HttpStatus.BAD_GATEWAY;

        HttpStatusCode statusCode = badGateway;

        System.out.println(statusCode.toString());
        System.out.println(badGateway.getReasonPhrase());

    }
}
