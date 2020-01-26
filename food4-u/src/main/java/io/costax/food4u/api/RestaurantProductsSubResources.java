package io.costax.food4u.api;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.restaurants.input.ProductInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.ProductOutputRepresentation;
import io.costax.food4u.api.openapi.controllers.RestaurantProductsSubResourcesOpenApi;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Product;
import io.costax.food4u.domain.repository.RestaurantRepository;
import io.costax.food4u.domain.services.RestaurantProductsRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(
        path = "/restaurants/{restaurantId}/products",
        produces = MediaType.APPLICATION_JSON_VALUE
        //consumes = MediaType.APPLICATION_JSON_VALUE
)
public class RestaurantProductsSubResources implements RestaurantProductsSubResourcesOpenApi {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantProductsRegistrationService restaurantProductsRegistrationService;

    @Autowired
    private Disassembler<Product, ProductInputRepresentation> disassembler;

    @Autowired
    private Assembler<ProductOutputRepresentation, Product> assembler;

    @GetMapping
    public List<ProductOutputRepresentation> list(@PathVariable Long restaurantId, @RequestParam Map<String, String> allParams) {
        List<Product> products = restaurantRepository.getRestaurantProducts(restaurantId);
        return assembler.toListOfRepresentations(products);
    }

    @GetMapping("/{productId}")
    public ProductOutputRepresentation findById(@PathVariable Long restaurantId, @PathVariable Long productId) {
        return restaurantRepository
                .findProduct(restaurantId, productId)
                .map(assembler::toRepresentation)
                .orElseThrow(() -> new ResourceNotFoundException(Product.class, productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductOutputRepresentation add(@PathVariable Long restaurantId,
                                           @RequestBody @Valid ProductInputRepresentation payload) {

        final Product product = restaurantProductsRegistrationService.add(restaurantId, disassembler.toDomainObject(payload));

        ResourceUriHelper.addUriInResponseHeader(product);

        return assembler.toRepresentation(product);
    }

    @PutMapping("/{productId}")
    public ProductOutputRepresentation update(@PathVariable Long restaurantId,
                                              @PathVariable Long productId,
                                              @RequestBody @Valid ProductInputRepresentation payload) {
        final Product product = restaurantProductsRegistrationService.update(restaurantId, productId, disassembler.toDomainObject(payload));
        return assembler.toRepresentation(product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long restaurantId,
                       @PathVariable Long productId) {
        restaurantProductsRegistrationService.delete(restaurantId, productId);
    }

}
