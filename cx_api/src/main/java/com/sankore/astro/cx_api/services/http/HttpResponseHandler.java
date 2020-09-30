package com.sankore.astro.cx_api.services.http;

import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.enums.ResponseCode;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Obi on 09/05/2019
 */
public interface HttpResponseHandler {

    void sendResponse(RoutingContext rc, BaseResponse response);

    void sendResponse(RoutingContext rc, ResponseCode responseCode);

    void sendResponse(RoutingContext rc, ResponseCode responseCode, String responseMessage);
}
