package com.sankore.astro.core;

import org.springframework.context.ApplicationContext;

/**
 * @author Obi on 10/05/2019
 */
public class AstroApplicationContext {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        AstroApplicationContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return AstroApplicationContext.applicationContext;
    }

    public static <T> T getServiceBean(String serviceName, Class<T> clazz) {
        return AstroApplicationContext.applicationContext.getBean(serviceName, clazz);
    }
}
