package io.github.jlmc.xsgoa.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Xsgoa sb3 operations",
                description = "Xsgoa sb3 operations is a poc of some of the technologies necessary to migrate a exiting project in a version bellow spring boot 3",
                license = @License(
                        name = "The Apache License, Version 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )

        )
)
public interface OpenAPIDefinitionDocumentation {
}
