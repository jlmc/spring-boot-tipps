package io.github.jlmc.reactive.core.router;

import io.github.jlmc.reactive.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.jlmc.reactive.ItemConstants.ITEM_FUNCTIONAL_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRouter(ItemsHandler itemsHandler) {
        return
                RouterFunctions
                        .route(GET(ITEM_FUNCTIONAL_V1).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems)
                        .andRoute(GET(ITEM_FUNCTIONAL_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem)
                        .andRoute(DELETE(ITEM_FUNCTIONAL_V1 + "/{id}"), itemsHandler::deleteItem)
                        .andRoute(POST(ITEM_FUNCTIONAL_V1).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::createItem)
                        .andRoute(PUT(ITEM_FUNCTIONAL_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::updateItem)
                ;


    }


    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET("/fun/runtime-exception")
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::itemRuntimeException);

    }

}
