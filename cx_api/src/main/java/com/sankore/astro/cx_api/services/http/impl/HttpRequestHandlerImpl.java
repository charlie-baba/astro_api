package com.sankore.astro.cx_api.services.http.impl;

import com.sankore.astro.core.AstroApplicationContext;
import com.sankore.astro.cx_api.services.http.HttpRequestHandler;
import com.sankore.astro.cx_api.services.http.HttpResponseHandler;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.generic.Constants;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.services.BaseEntityService;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @author Obi on 09/05/2019
 */
@Service
public class HttpRequestHandlerImpl implements HttpRequestHandler {

    @Autowired
    Environment env;

    @Autowired
    AstroLoggerService log;

    @Autowired
    HttpResponseHandler httpResponseHandler;

    @Autowired
    I18nMessagingService messagingService;

    @Override
    public void parseContent(RoutingContext rc) {
        try {
            String objString = rc.getBodyAsString();
            log.info("Request: "+ objString);
            if (StringUtils.isBlank(objString)) {
                httpResponseHandler.sendResponse(rc, ResponseCode.Empty_Request);
                return;
            }

            String className = rc.getBodyAsJson().getString(Constants.RequestKey.REQUEST_CONTENT_CLASS);
            String handlerMethod = rc.getBodyAsJson().getString(Constants.RequestKey.REQUEST_HANDLER_METHOD);
            Class requestPojoClass = findClass(className);
            if (requestPojoClass == null) {
                httpResponseHandler.sendResponse(rc, ResponseCode.Bad_Request, messagingService.getMessage("invalid_content_class"));
                return;
            }

            Object pojoObject = Json.decodeValue(objString, requestPojoClass);
            if (pojoObject == null) {
                httpResponseHandler.sendResponse(rc, ResponseCode.Bad_Request, messagingService.getMessage("unable_to_parse_request"));
                return;
            }

            AstroApplicationContext
                    .getServiceBean(env.getProperty(className +"_Service"), BaseEntityService.class)
                    .processRequest(pojoObject, handlerMethod, resp -> completeRequest(rc, resp));
        } catch (Exception e) {
            log.error("Error", e);
            httpResponseHandler.sendResponse(rc, ResponseCode.Internal_Server_Error, e.getMessage());
        }
    }

    private Class findClass(String className) {
        if (!StringUtils.isBlank(className)) {
            try {
                return Class.forName(env.getProperty(className));
            } catch (ClassNotFoundException e) {
                log.error("Error", e);
            }
        }
        return null;
    }

    private void completeRequest(RoutingContext rc, AsyncResult<BaseResponse> resp) {
        log.logJSON("Response: ", resp.result());
        if (resp.succeeded())
            httpResponseHandler.sendResponse(rc, resp.result());
        else
            httpResponseHandler.sendResponse(rc, ResponseCode.Internal_Server_Error);
    }
}
