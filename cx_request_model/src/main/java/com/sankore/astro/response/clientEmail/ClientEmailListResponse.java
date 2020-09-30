package com.sankore.astro.response.clientEmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.EmailDetail;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 30/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientEmailListResponse extends BaseResponse {

    private List<EmailDetail> emailDetails;

    public ClientEmailListResponse() { }

    public ClientEmailListResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
