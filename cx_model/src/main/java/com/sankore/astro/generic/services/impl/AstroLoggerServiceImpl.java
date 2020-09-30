package com.sankore.astro.generic.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankore.astro.generic.services.AstroLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Obi on 09/05/2019
 */
@Service
public class AstroLoggerServiceImpl implements AstroLoggerService {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Autowired
    private ObjectMapper objectMapper;

    public void  info(String infoMessage){
        logger.info(infoMessage);
    }

    public void info(String object, Throwable exce){
        logger.info(object,exce);
    }

    public void warn(String object){
        logger.warn(object);
    }

    public void warn(String object, Throwable exce){
        logger.warn(object,exce);
    }

    public void error(String object, Throwable exce){
        logger.error(object,exce);
    }

    public void error(String object){
        logger.error(object);
    }

    public void trace(String object, Throwable exce){
        logger.trace(object,exce);
    }

    public void trace(String object){
        logger.trace(object);
    }

    public boolean isDebugEnabled(){
        return logger.isDebugEnabled();
    }

    public boolean isWarnEnabled(){
        return logger.isWarnEnabled();
    }

    public boolean isTraceEnabled(){
        return logger.isTraceEnabled();
    }

    public void logJSON(String prefix, Object object){
        try {
            logger.info(prefix +" "+ objectMapper.writeValueAsString(object));
        } catch(Exception err){
            logger.error("Error", err);
        }
    }
    public ObjectMapper getObjectMapper(){
        return this.objectMapper;
    }
}
