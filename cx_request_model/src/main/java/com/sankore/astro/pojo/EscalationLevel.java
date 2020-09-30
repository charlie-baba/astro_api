package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Obi on 14/08/2020
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EscalationLevel implements Serializable {

    private long slaId;

    private int level;

    private int period;

    private String timeUnit;

    private Set<UserDetail> admins;
}
