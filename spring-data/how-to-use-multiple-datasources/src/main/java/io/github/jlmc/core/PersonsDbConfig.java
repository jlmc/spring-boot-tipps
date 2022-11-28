package io.github.jlmc.core;

import io.github.jlmc.domain.entities.persons.Person;
import io.github.jlmc.domain.repositories.persons.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackageClasses = PersonRepository.class,
        entityManagerFactoryRef = "personsEntityManager"
        , transactionManagerRef = "personsTransactionManager"
)
public class PersonsDbConfig {

    @Autowired
    Environment env;

    @Bean
    @ConfigurationProperties(prefix = "persons.datasource")
    public DataSource personsDatasource() {
        return DataSourceBuilder.create()
                .build();
    }

    @Bean
    @Qualifier("personsEntityManager")
    public LocalContainerEntityManagerFactoryBean personsEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("personsDatasource") DataSource dataSource) {


        final Map<String, String> vendorConfigurations = new HashMap<>();
        vendorConfigurations.put("javax.persistence.schema-generation.database.action", "drop-and-create");

        return builder.dataSource(dataSource)
                .packages(Person.class)
                .properties(vendorConfigurations)
                .build();
    }

    @Bean
    @Qualifier("personsTransactionManager")
    public PlatformTransactionManager personsTransactionManager(
            @Qualifier("personsEntityManager") LocalContainerEntityManagerFactoryBean personsEntityManager) {
        return new JpaTransactionManager(personsEntityManager.getObject());
    }

}
