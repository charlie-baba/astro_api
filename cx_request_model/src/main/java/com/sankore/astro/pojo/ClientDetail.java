package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Obi on 23/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDetail implements Serializable {

    private long id;

    private String name;

    private String shortName;

    private String code;

    private String email;

    private String logo;

    private String template;

    private boolean active;
}
