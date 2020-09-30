package com.sankore.astro.request.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 23/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FetchClientRequest extends BaseRequest {

    private long clientId;

    public FetchClientRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
