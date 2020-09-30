package com.sankore.astro.cx_api.services.http;

import io.vertx.ext.web.RoutingContext;

/**
 * @author Obi on 09/05/2019
 */
public interface HttpRequestHandler {

    void parseContent(RoutingContext routingContext);
}
