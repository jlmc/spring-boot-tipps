package io.github.jlmc.core;

import com.zaxxer.hikari.HikariDataSource;
import io.github.jlmc.domain.entities.books.Book;
import io.github.jlmc.domain.repositories.books.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackageClasses = BookRepository.class,
        entityManagerFactoryRef = "booksEntityManager"
        , transactionManagerRef = "booksTransactionManager"
)
public class BookDbConfig {

    @Autowired
    Environment env;



    @Bean
    @Qualifier("booksDatasource")
    @Primary // master app datasource
    //@ConfigurationProperties("books.datasource.configuration")
    @ConfigurationProperties(prefix = "books.datasource")
    public DataSource booksDatasource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
        return dataSource;

        /*
        return memberDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
                .build();*/


    }

    @Bean
    @Qualifier("booksEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean booksEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("booksDatasource") DataSource dataSource) {

        final Map<String, String> vendorConfigurations = new HashMap<>();
        vendorConfigurations.put("javax.persistence.schema-generation.database.action", "drop-and-create");

        return builder.dataSource(dataSource)
                .packages(Book.class)
                .properties(vendorConfigurations)
                .build();
    }

    @Bean
    @Primary
    @Qualifier("booksTransactionManager")
    public PlatformTransactionManager booksTransactionManager(
            LocalContainerEntityManagerFactoryBean booksEntityManager) {
        return new JpaTransactionManager(booksEntityManager.getObject());
    }

}
