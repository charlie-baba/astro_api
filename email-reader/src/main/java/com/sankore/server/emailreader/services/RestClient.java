package com.sankore.server.emailreader.services;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import lombok.extern.log4j.Log4j;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;

/**
 * @author Obi on 12/05/2019
 */
@Log4j
@Service
public class RestClient<T> {

    private Class<T> clazz;
    public static Client client;

    public final void setClazz(Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    private static Client getClient() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(JacksonJsonProvider.class);

        if (client == null) {
            client = Client.create(clientConfig);
        } else {
            client.destroy();
            client = Client.create(clientConfig);
        }
        return client;
    }

    public T postJson(String url, Object req, String authorization) {
        try {
            WebResource webResource = getClient().resource(url);
            return (T) webResource.accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorization)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .post(clazz, req);
        } catch(Exception e) {
            log.error("Unable to post json: ", e);
            return null;
        } finally {
            client.destroy();
        }
    }

    public T postJson(String url, String authorization) {
        try {
            WebResource webResource = getClient().resource(url);
            return (T) webResource.accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorization).post(clazz);
        } catch(Exception e) {
            log.error("Unable to post json: ", e);
            return null;
        } finally {
            client.destroy();
        }
    }

    public T getJson(String url, String authorization) {
        try {
            WebResource webResource = getClient().resource(url);
            return (T) webResource.accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorization)
                    .header("Content-Type", MediaType.APPLICATION_JSON).get(clazz);
        } catch(Exception e) {
            log.error("Unable to get json: ", e);
            return null;
        } finally {
            client.destroy();
        }
    }

}
