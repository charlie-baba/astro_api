package com.sankore.astro.request.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Obi on 20/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FetchTicketsRequest extends BaseRequest {

	private static final long serialVersionUID = -7706223162515614730L;

	private Map<String, Object> filters;

    private int start;

    private int size;
    
    private boolean isAdmin;

    public FetchTicketsRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
