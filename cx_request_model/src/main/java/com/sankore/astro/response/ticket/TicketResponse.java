package com.sankore.astro.response.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.FilterParams;
import com.sankore.astro.pojo.TicketDetail;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 20/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketResponse extends BaseResponse {

    private FilterParams filterParams;

    private List<TicketDetail> ticketDetails;

    private long totalCount;

    public TicketResponse() {}

    public TicketResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
