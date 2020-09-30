package com.sankore.astro.generic.services;

import java.util.Locale;

/**
 * @author Obi on 09/05/2019
 */
public interface I18nMessagingService {

    String getMessage(String key);

    String getMessage(String key, Locale locale);

    String getMessage(String key ,Object[] params);

    String getMessage(String key, Object[] params, Locale locale);

}
