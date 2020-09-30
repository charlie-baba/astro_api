package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author Obi on 06/06/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category implements Serializable {

    private long id;

    private String name;

    private String description;

    private List<IssueTypePojo> issueTypes;
}
