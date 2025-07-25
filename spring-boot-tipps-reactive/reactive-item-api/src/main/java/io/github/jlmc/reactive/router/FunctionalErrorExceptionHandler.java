package io.github.jlmc.reactive.router;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class FunctionalErrorExceptionHandler extends AbstractErrorWebExceptionHandler {


    public FunctionalErrorExceptionHandler(ErrorAttributes errorAttributes,
                                           ApplicationContext applicationContext,
                                           ServerCodecConfigurer serverCodecConfigurer
                                           ) {
        super(errorAttributes,
                new WebProperties.Resources(),
                applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions
                .route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
        Map<String, Object> errorAttributes = getErrorAttributes(serverRequest, ErrorAttributeOptions.defaults());

        log.info("renderErrorResponse: {}", errorAttributes);

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorAttributes);
    }
}
