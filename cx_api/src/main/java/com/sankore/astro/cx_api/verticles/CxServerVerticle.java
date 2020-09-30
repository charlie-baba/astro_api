package com.sankore.astro.cx_api.verticles;

import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.cx_api.services.http.HttpRequestHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Obi on 07/05/2019
 */
@Component
public class CxServerVerticle extends AbstractVerticle {

    @Autowired
    HttpRequestHandler httpRequestHandler;

    @Autowired
    AstroLoggerService log;

    @Value("${api_server_port}")
    private int port;

    @Override
    public void start(Future<Void> fut) throws Exception {
        super.start();

        log.info("-----------------application starting........................."+ port);
        Router router = Router.router(vertx);
        router.route("/*").handler(ResponseContentTypeHandler.create());
        router.route(HttpMethod.POST, "/*").handler(BodyHandler.create());

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html")
                    .end("<h1> Welcome to AstroCX </h1>");
        });

        router.route("/api/cx/requesthandler").handler(rc -> httpRequestHandler.parseContent(rc));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, hr -> completeStartup(hr, fut));
    }

    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }
}
