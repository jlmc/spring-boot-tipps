package io.github.jlmc.reactive.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

@Component
public class LowercaseWebFilter implements WebFilter {

    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public Mono<Void> filter(ServerWebExchange currentRequest, WebFilterChain chain) {


        final RequestPath path = currentRequest.getRequest().getPath();
        final HttpMethod method = currentRequest.getRequest().getMethod();
        System.out.println("===> " + method + " " + path);

        return requestMappingHandlerMapping.getHandler(currentRequest)
                                    .filter(e -> e instanceof HandlerMethod)
                                    .flatMap(e -> {

                                        System.out.println("----->> ");

                                        return chain.filter(currentRequest);

                                    })
                                           .switchIfEmpty(


                                                   chain(currentRequest, chain));
        /*
        var lowercaseUri =
                URI.create(currentRequest.getRequest()
                                         .getURI()
                                         .toString()
                                         .toLowerCase());

         */


        //final ServerWebExchange build = currentRequest.mutate().request(builder -> builder.uri(lowercaseUri)).build();

        //return chain.filter(currentRequest);
    }

    private Mono<Void> chain(ServerWebExchange currentRequest, WebFilterChain chain) {

        System.out.println("====> faulback handler");
        return chain.filter(currentRequest);
    }
}
