package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Obi on 07/06/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sla implements Serializable {

    private long id;

    private long issueTypeId;

    private String issueTypeName;

    private long categoryId;

    private String categoryName;

    private long clientId;

    private Set<EscalationLevel> escalationLevels = new HashSet<>();
}
