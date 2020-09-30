package com.sankore.astro.response.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.ClientDetail;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 23/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientListResponse extends BaseResponse {

    private List<ClientDetail> clientDetails;

    public ClientListResponse() { }

    public ClientListResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
