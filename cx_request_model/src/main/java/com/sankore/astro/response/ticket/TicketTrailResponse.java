package com.sankore.astro.response.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Trail;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 06/06/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketTrailResponse extends BaseResponse {

    private List<Trail> trails;

    public TicketTrailResponse() { }

    public TicketTrailResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
