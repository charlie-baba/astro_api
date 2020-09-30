package com.sankore.astro.generic.services.impl;

import com.sankore.astro.generic.services.I18nMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author Obi on 09/05/2019
 */
@Service
public class I18nMessagingServiceImpl implements I18nMessagingService {

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    private Locale defaultLocale = Locale.ENGLISH;

    public String getMessage(String key){
        return messageSource.getMessage(key,new Object[0], defaultLocale);
    }

    public String getMessage(String key, Locale locale){
        return messageSource.getMessage(key, new Object[0], locale);
    }

    public String getMessage(String key ,Object[] params){
        return messageSource.getMessage(key, params, defaultLocale);
    }

    public String getMessage(String key, Object[] params, Locale locale){
        return messageSource.getMessage(key, params, locale);
    }

}
