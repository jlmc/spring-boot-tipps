package io.costax.food4u.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RootEntryPointResources {

    @Autowired
    private ApiLinks apiLinks;

    @GetMapping
    public RootEntryPointModel root() {
        var rootEntryPointModel = new RootEntryPointModel();

        rootEntryPointModel.add(apiLinks.linkToCookers("cookers"));
        rootEntryPointModel.add(apiLinks.linkToGroups("groups"));
        rootEntryPointModel.add(apiLinks.linkToPaymentMethods("payment-methods"));
        rootEntryPointModel.add(apiLinks.linkToRestaurants("restaurants"));
        rootEntryPointModel.add(apiLinks.linkToUsers("users"));
        rootEntryPointModel.add(apiLinks.linkToRequests("requests"));
        rootEntryPointModel.add(apiLinks.linkToStatistics("statistics"));

        return rootEntryPointModel;
    }

    private static class RootEntryPointModel extends RepresentationModel<RootEntryPointModel> {
    }

}
