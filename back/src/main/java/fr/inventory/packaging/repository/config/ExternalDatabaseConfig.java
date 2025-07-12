package fr.inventory.packaging.repository.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "fr.inventory.packaging.repository.external",
        entityManagerFactoryRef = "externalEntityManagerFactory",
        transactionManagerRef = "externalTransactionManager"
)
public class ExternalDatabaseConfig {
    @Value("${EXTERNAL_DATASOURCE_URL}")
    private String dbUrl;

    @Value("${EXTERNAL_DATASOURCE_USERNAME}")
    private String dbUsername;

    @Value("${EXTERNAL_DATASOURCE_PASSWORD}")
    private String dbPassword;

    @Value("${EXTERNAL_DATASOURCE_DRIVER_CLASS_NAME}")
    private String driver;

    @Bean(name = "externalDataSource")
    public DataSource externalDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    @Bean(name = "externalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean externalEntityManagerFactory(
            @Qualifier("externalDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.community.dialect.FirebirdDialect");
        return builder
                .dataSource(dataSource)
                .packages("fr.inventory.packaging.entity.external")
                .persistenceUnit("external")
                .properties(jpaProperties)
                .build();
    }

    @Bean(name = "externalTransactionManager")
    public PlatformTransactionManager externalTransactionManager(
            @Qualifier("externalEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
