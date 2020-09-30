package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Obi on 15/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

	private long id;

    private String name;

    private String description;

    private long clientId;

    private String clientCode;

    private long usersCount;
    
    private boolean active;

    private List<Category> categories;
}
