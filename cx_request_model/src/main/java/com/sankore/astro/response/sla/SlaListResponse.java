package com.sankore.astro.response.sla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Sla;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 07/06/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlaListResponse extends BaseResponse {

    private List<Sla> slas;

    public SlaListResponse() {}

    public SlaListResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
