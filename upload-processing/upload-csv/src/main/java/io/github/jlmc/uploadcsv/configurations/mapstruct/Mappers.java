package io.github.jlmc.uploadcsv.configurations.mapstruct;

import io.github.jlmc.uploadcsv.locations.boundary.mappers.LocationResourceMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Mappers {

    @Bean
    public LocationResourceMapper locationResourceMapper() {
        return LocationResourceMapper.MAPPER;
    }
}
