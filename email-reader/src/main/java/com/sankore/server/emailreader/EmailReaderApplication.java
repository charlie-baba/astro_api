package com.sankore.server.emailreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@PropertySources({
        @PropertySource(value = {"classpath:application.properties"}),
        @PropertySource(value = {"file:${APP_SERVICE_CONFIG}/cx/app-conf.properties"})
})
@SpringBootApplication(scanBasePackages={"com.sankore.server.emailreader*"}, exclude = {DataSourceAutoConfiguration.class})
public class EmailReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailReaderApplication.class, args);
    }

}
