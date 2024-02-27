package io.github.jlmc.cwr.service.api.configurations;


import io.github.jlmc.cwr.service.common.ConfigurationRepresentation;
import io.github.jlmc.cwr.service.domain.configurations.ConfigurationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/configurations")
public class ConfigurationsController {


    private final ConfigurationsService service;

    public ConfigurationsController(ConfigurationsService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConfigurationRepresentation> getAll() {
        return service.getAllConfigurations()
                .stream()
                .map(it -> new ConfigurationRepresentation(it.getName(), it.getValue()))
                .toList();
    }
}
