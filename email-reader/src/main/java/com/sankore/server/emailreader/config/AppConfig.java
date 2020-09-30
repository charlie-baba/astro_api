package com.sankore.server.emailreader.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Obi on 28/05/2019
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.sankore.server.emailreader.repository", transactionManagerRef = "txManage")
@EntityScan("com.sankore.server.emailreader.entity")
@EnableTransactionManagement
public class AppConfig {

    @Autowired
    Environment env;

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("astroPU");
        em.setPackagesToScan(new String[]{"com.sankore.server.emailreader.entity"});
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        ((HibernateJpaVendorAdapter) vendorAdapter).setDatabasePlatform(env.getProperty("hibernate.dialect"));
        em.setJpaVendorAdapter(vendorAdapter);
        em.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        Map<String,Object> jpaPropertyMap = new HashMap<>();
        jpaPropertyMap.put("javax.persistence.validation.mode", env.getProperty("javax.persistence.validation.mode"));
        jpaPropertyMap.put("hibernate.show_sql", env.getProperty("hibernate.show.sql"));
        jpaPropertyMap.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        jpaPropertyMap.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        jpaPropertyMap.put("hibernate.jdbc.batch_size", env.getProperty("hibernate.jdbc.batch_size"));
        jpaPropertyMap.put("hibernate.hikari.dataSource.url", env.getProperty("astro.jdbc.url"));
        jpaPropertyMap.put("hibernate.hikari.dataSource.user", env.getProperty("astro.jdbc.username"));
        jpaPropertyMap.put("hibernate.hikari.dataSource.password", env.getProperty("astro.jdbc.password"));
        jpaPropertyMap.put("hibernate.hikari.dataSourceClassName", env.getProperty("astro.jdbc.datasource.class"));
        jpaPropertyMap.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        jpaPropertyMap.put("hibernate.hikari.maxLifetime", env.getProperty("astro.jdbc.maxLifetime"));
        jpaPropertyMap.put("hibernate.hikari.idleTimeout", env.getProperty("astro.jdbc.maxidletime"));
        jpaPropertyMap.put("hibernate.hikari.connectionTimeout", env.getProperty("astro.jdbc.connectTimeout"));
        jpaPropertyMap.put("hibernate.hikari.maximumPoolSize", env.getProperty("astro.jdbc.maxPoolSize"));
        jpaPropertyMap.put("hibernate.hikari.minimumIdle", env.getProperty("astro.jdbc.minPoolSize"));

        em.setJpaPropertyMap(jpaPropertyMap);
        em.setJpaDialect(new HibernateJpaDialect());
        em.setPersistenceUnitRootLocation("classpath:/com/sankore/server/emailreader/entity");
        em.setPersistenceProvider(new HibernatePersistenceProvider());
        return em;
    }

    @Bean(name = "txManage")
    public PlatformTransactionManager transactionManager(@Autowired EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
