package io.costax.food4u.core.openapi;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Header;
import springfox.documentation.service.ResponseMessage;

import java.util.List;
import java.util.Map;

public class GlobalHttpVerbResponseMessages {

    static List<ResponseMessage> getGetHttpVerbResponseMessages() {

        return List.of(

                new ResponseMessageBuilder()
                        .code(HttpStatus.OK.value())
                        .message("The Success response")
                        .build(),

                new ResponseMessageBuilder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Resource not found")
                        .build(),

                new ResponseMessageBuilder()
                        .code(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Resource do not contain representation for the required type")
                        .build()
        );
    }

    /**
     * Define the HTTP global response status documentation for the POST HTTP verb
     * <pre>
     *
     * - 400 (Bad Request)
     * - 500 (Internal Server Error)
     * - 406 (Not Acceptable)
     * - 415 (Unsupported Media Type)
     * </pre>
     */
    static List<ResponseMessage> getPostHttpVerbResponseMessages() {
        return List.of(
                new ResponseMessageBuilder()
                        .code(HttpStatus.CREATED.value())
                        .message("Resource has been created")
                        .headersWithDescription(
                                Map.of(
                                        HttpHeaders.LOCATION,
                                        new Header(HttpHeaders.LOCATION,
                                                "The created Resource can be found in the Location",
                                                new ModelRef("Uri"))))
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid request (client error)")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Server expected error")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Resource has no representation that could be accepted by the consumer")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .message("Request declined because the body is in an unsupported format")
                        .build()
        );
    }

    /**
     * Define the HTTP global response status documentation for the PUT HTTP verb
     * <pre>
     *
     * - 400 (Bad Request)
     * - 500 (Internal Server Error)
     * - 406 (Not Acceptable)
     * - 415 (Unsupported Media Type)
     * </pre>
     */
    static List<ResponseMessage> getPutHttpVerbResponseMessages() {

        return List.of(
                new ResponseMessageBuilder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid request (client error)")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Server expected error")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Resource has no representation that could be accepted by the consumer")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .message("Request declined because the body is in an unsupported format")
                        .build()
        );
    }

    /**
     * Define the HTTP global response status documentation for the Delete HTTP verb
     * <pre>
     * - 400 (Bad Request)
     * - 500 (Internal Server Error)
     * </pre>
     */
    static List<ResponseMessage> getDeleteHttpVerbResponseMessages() {
        return List.of(
                new ResponseMessageBuilder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid request (client error)")
                        .build(),
                new ResponseMessageBuilder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Server expected error")
                        .build()
        );
    }
}
