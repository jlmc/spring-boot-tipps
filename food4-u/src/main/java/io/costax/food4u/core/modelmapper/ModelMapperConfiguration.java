package io.costax.food4u.core.modelmapper;

import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Cooker.class, CookerOutputRepresentation.class)
                .addMapping(Cooker::getName, CookerOutputRepresentation::setTitle);
        modelMapper.createTypeMap(CookerInputRepresentation.class, Cooker.class)
                .addMapping(CookerInputRepresentation::getTitle, Cooker::setName);

        return modelMapper;
    }
}
