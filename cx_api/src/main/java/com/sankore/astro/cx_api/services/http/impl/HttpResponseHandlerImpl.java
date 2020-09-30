package com.sankore.astro.cx_api.services.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.cx_api.services.http.HttpResponseHandler;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.generic.services.AstroLoggerService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Obi on 09/05/2019
 */
@Service
public class HttpResponseHandlerImpl implements HttpResponseHandler {

    @Autowired
    AstroLoggerService log;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void sendResponse(RoutingContext rc, BaseResponse response) {
        try {
            rc.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(mapper.writeValueAsString(response));
        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    @Override
    public void sendResponse(RoutingContext rc, ResponseCode responseCode) {
        rc.response()
                .setStatusCode(responseCode.getCode())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end("{\"description\":  "+ responseCode.getMessage() + "}");
    }

    @Override
    public void sendResponse(RoutingContext rc, ResponseCode responseCode, String responseMessage) {
        rc.response()
                .setStatusCode(responseCode.getCode())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end("{\"description\": "+ responseMessage + "}");
    }
}
