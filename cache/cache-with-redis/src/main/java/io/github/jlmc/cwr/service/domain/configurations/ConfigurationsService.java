package io.github.jlmc.cwr.service.domain.configurations;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConfigurationsService {

    private final ConfigurationRepository repository;

    public ConfigurationsService(ConfigurationRepository repository) {
        this.repository = repository;
    }

    public List<Configuration> getAllConfigurations() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }
}

