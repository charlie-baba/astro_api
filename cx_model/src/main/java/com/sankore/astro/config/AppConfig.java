package com.sankore.astro.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Obi on 08/05/2019
 */
@Configuration
@EntityScan("com.sankore.astro.entity")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.sankore.astro.repository", transactionManagerRef = "txManage")
@PropertySource(value = {"file:${APP_SERVICE_CONFIG}/cx/app-conf.properties",
        "file:${APP_SERVICE_CONFIG}/cx/i18n/message.properties"})
public class AppConfig {

    @Autowired
    Environment env;

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("astroPU");
        em.setPackagesToScan(new String[]{"com.sankore.astro.entity"});
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
        em.setPersistenceUnitRootLocation("classpath:/com/sankore/astro/entity");
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

    @Bean(name = "messageSource")
    public MessageSource createMessageResource(){
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        String basenName[] = new String[]{"file:"+ System.getenv("APP_SERVICE_CONFIG") +"/cx/i18n/message"};
        reloadableResourceBundleMessageSource.setBasenames(basenName);
        reloadableResourceBundleMessageSource.setConcurrentRefresh(false);
        reloadableResourceBundleMessageSource.setCacheSeconds(-1);
        reloadableResourceBundleMessageSource.setFallbackToSystemLocale(false);
        return reloadableResourceBundleMessageSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(env.getProperty("jms.broker"));
        connectionFactory.setUserName(env.getProperty("jms.connectionfactory.username"));
        connectionFactory.setPassword(env.getProperty("jms.connectionfactory.password"));
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    @Bean(name = "jmsConnectionFactory")
    public PooledConnectionFactory jmsConnectionFactory() {
        String maxCon = env.getProperty("jms.maxconnection");
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setMaxConnections(Integer.parseInt(maxCon == null ? "8" : maxCon));
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        return pooledConnectionFactory;
    }

    @Bean(name = "jmsTransactionManager")
    public JmsTransactionManager jmsTransactionManager(@Autowired ConnectionFactory connectionFactory) {
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory);
        return  transactionManager;
    }

    @Bean(name = "astroEmailQueue")
    public ActiveMQQueue astroEmailQueue(@Value("LigareEmailQueue") String queueName) {
        return new ActiveMQQueue(queueName);
    }

    @Bean(name = "astroSMSQueue")
    public ActiveMQQueue astroSMSQueue(@Value("LigareSMSQueue") String queueName) {
        return new ActiveMQQueue(queueName);
    }

    @Bean(name = "emailQueueJmsTemplate")
    public JmsTemplate emailQueueJmsTemplate(@Autowired ConnectionFactory jmsConnectionFactory,
                                             @Autowired @Qualifier("astroEmailQueue") Destination destination) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(jmsConnectionFactory);
        jmsTemplate.setReceiveTimeout(1);
        jmsTemplate.setDefaultDestination(destination);
        return jmsTemplate;
    }

    @Bean(name = "smsQueueJmsTemplate")
    public JmsTemplate smsQueueJmsTemplate(@Autowired ConnectionFactory jmsConnectionFactory,
                                           @Autowired @Qualifier("astroSMSQueue") Destination destination) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(jmsConnectionFactory);
        jmsTemplate.setReceiveTimeout(1);
        jmsTemplate.setDefaultDestination(destination);
        return jmsTemplate;
    }
}
