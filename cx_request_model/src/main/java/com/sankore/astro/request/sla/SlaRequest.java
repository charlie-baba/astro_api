package com.sankore.astro.request.sla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.pojo.Sla;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 07/06/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SlaRequest extends BaseRequest {

    private Sla sla;

    public SlaRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
