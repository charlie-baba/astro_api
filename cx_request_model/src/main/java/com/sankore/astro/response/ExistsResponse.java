package com.sankore.astro.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 10/08/2020
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExistsResponse extends BaseResponse {

    private boolean exists;

    public ExistsResponse() { }

    public ExistsResponse(int responseCode, String responseMessage) {
        this.setResponseCode(responseCode);
        this.setResponseMessage(responseMessage);
    }

    public ExistsResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
