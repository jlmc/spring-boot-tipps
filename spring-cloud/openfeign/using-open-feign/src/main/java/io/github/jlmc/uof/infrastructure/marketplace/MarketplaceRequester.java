package io.github.jlmc.uof.infrastructure.marketplace;

import io.github.jlmc.uof.domain.fruits.entities.PriceDetail;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.MarketProductInformationProvider;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * see <a href="https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html">spring-cloud-openfeign</a>
 */
@FeignClient(
        name = "marketplaceRequester"
        //url = "localhost:8081/marketplace"
)
public interface MarketplaceRequester {

    @GetMapping(path = "/products/{product-id}/best-price")
    PriceDetail getProductBestPrice(@PathVariable("product-id") String productId);


    @PostMapping(path = "/reservations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    MarketProductInformationProvider.OrderReservationResponse createReservation(@RequestBody MarketProductInformationProvider.OrderReservationRequest request);
}
