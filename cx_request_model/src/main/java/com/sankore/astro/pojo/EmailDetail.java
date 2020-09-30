package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Obi on 30/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailDetail {

    private long id;

    private String email;

    private String password;

    private String server;

    private int port;

    private boolean active;

    private String protocol;

    private long clientId;

    private String clientCode;
}
