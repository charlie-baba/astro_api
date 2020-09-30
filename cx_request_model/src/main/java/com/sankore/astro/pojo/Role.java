package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Obi on 15/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role implements Serializable {

    private long id;

    private String name;

    private String description;

    private long clientId;

    private boolean system;

    private long noOfUsers;

    private boolean active;

}
