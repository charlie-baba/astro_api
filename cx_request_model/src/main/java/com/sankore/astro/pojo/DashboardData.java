package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author Obi on 16/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardData implements Serializable {

	private static final long serialVersionUID = -4855304119216789521L;

	private long unresolved;

    private long overdue;

    private long inProgress;

    private long dueToday;

    private long resolved;

    private List<Object[]> categories;

    private String channels;

    private String media;
}
