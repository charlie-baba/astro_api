package com.sankore.astro.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.DashboardData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 16/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardResponse extends BaseResponse {

    private DashboardData data;

    public DashboardResponse() { }

    public DashboardResponse(int responseCode, String responseMessage) {
        this.setResponseCode(responseCode);
        this.setResponseMessage(responseMessage);
    }

    public DashboardResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
