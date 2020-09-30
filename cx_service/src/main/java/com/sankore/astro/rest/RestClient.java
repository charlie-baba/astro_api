package com.sankore.astro.rest;

import com.sankore.astro.generic.services.AstroLoggerService;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Obi on 02/09/2020
 */
@Service
public class RestClient<T> {

    private Class<T> clazz;
    public static Client client;

    @Autowired
    AstroLoggerService log;

    public final void setClazz(Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    private static Client getClient() {
        client = ClientBuilder.newClient();
        return client;
    }

    public T postJson(String url, Object req, String authorization) {
        try {
            return getClient()
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorization)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .post(Entity.entity(req, MediaType.APPLICATION_JSON), clazz);
        } catch(Exception e) {
            log.error("Unable to post json: ", e);
            return null;
        }
    }

    public T getJson(String baseUrl, String path, String authorization) {
        try {
            log.info("fetching from "+ baseUrl + path);
            return getClient()
                    .target(baseUrl)
                    .path(path)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorization)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .get(clazz);
        } catch(Throwable e) {
            log.error("Unable to get json: ", e);
            return null;
        }
    }
}
