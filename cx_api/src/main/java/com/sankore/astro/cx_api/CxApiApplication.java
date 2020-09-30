package com.sankore.astro.cx_api;

import com.sankore.astro.core.AstroApplicationContext;
import com.sankore.astro.cx_api.verticles.CxServerVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableScheduling
@PropertySources({
        @PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = true),
        @PropertySource(value = {"file:${APP_SERVICE_CONFIG}/cx/app-conf.properties"}),
        @PropertySource(value = {"file:${APP_SERVICE_CONFIG}/cx/resource-map.properties"})
})
@SpringBootApplication(scanBasePackages = {"com.sankore.astro.*"}, exclude = {DataSourceAutoConfiguration.class})
public class CxApiApplication {

    @Autowired
    CxServerVerticle cxServerVerticle;

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(CxApiApplication.class, args);
    }

    @PostConstruct
    public void deployServerVerticle() {
        AstroApplicationContext.setApplicationContext(applicationContext);
        Vertx.vertx().deployVerticle(cxServerVerticle);
    }

}
