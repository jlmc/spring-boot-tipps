package io.costax.food4u.core.modelmapper;

import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.api.model.users.input.UserInputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.model.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    private static final Converter<String, String> TO_LOWER_CASE = ctx -> ctx.getSource() == null ? null : ctx.getSource().toLowerCase();

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        //.setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        modelMapper.createTypeMap(Cooker.class, CookerOutputRepresentation.class)
                .addMapping(Cooker::getName, CookerOutputRepresentation::setTitle);
        modelMapper.createTypeMap(CookerInputRepresentation.class, Cooker.class)
                .addMapping(CookerInputRepresentation::getTitle, Cooker::setName);

        /*
        // example of custom mapping
        var customTypeMap = modelMapper.createTypeMap(Address.class, AddressModel.class);
        AddressToAddressModelTypeMap.<String>addMapping(
                AddressSrc -> AddressSrc.getCity().getState().getName(),
                (AddressModelDest, value) -> AddressModelDest.getCity().setState(value));
         */

        final TypeMap<UserInputRepresentation, User> typeMap = modelMapper.createTypeMap(UserInputRepresentation.class, User.class);

        typeMap.addMappings(
                // toUppercase will be called with the property types from getFirstName() and setName()
                mapper -> mapper.using(TO_LOWER_CASE)
                        .map(UserInputRepresentation::getEmail, User::setEmail));


        return modelMapper;
    }
}
