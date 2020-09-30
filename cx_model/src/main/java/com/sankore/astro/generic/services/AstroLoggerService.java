package com.sankore.astro.generic.services;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Obi on 09/05/2019
 */
public interface AstroLoggerService {

    void  info(String object);

    void info(String object, Throwable exce);

    void warn(String object);

    void warn(String object, Throwable exce);

    void error(String object, Throwable exce);

    void error(String object);

    void trace(String object, Throwable exce);

    void trace(String object);

    void logJSON(String prefix, Object object);

    boolean isDebugEnabled();

    boolean isWarnEnabled();

    boolean isTraceEnabled();

    ObjectMapper getObjectMapper();
}
