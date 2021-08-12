package org.chxei.shmessenger.config.database;


import io.github.cdimascio.dotenv.Dotenv;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableTransactionManagement
public class PsqlConfig {
    Dotenv dotenv = Misc.dotenv;

    String DB_JDBC_URL = Stream.of(dotenv.get("DB_JDBC_URL"))
            .map(str -> str.replace("<DB_HOST>", dotenv.get("DB_HOST"))
                    .replace("<DB_PORT>", dotenv.get("DB_PORT"))
                    .replace("<DB_DATABASE>", dotenv.get("DB_DATABASE"))
                    .replace("<DB_USER>", dotenv.get("DB_USER"))
                    .replace("<DB_PASSWORD>", dotenv.get("DB_PASSWORD")))
            .collect(Collectors.joining());


    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("org.chxei.shmessenger");
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
        hibernateProperties.put("hibernate.show_sql", "true");
        hibernateProperties.put("hibernate.hbm2ddl.auto", "update"); //update,create-drop, create, validate, none
        hibernateProperties.put("hibernate.current_session_context_class", "thread");

        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(DB_JDBC_URL);
        dataSource.setUsername(dotenv.get("DB_USER"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
