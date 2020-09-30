package com.sankore.astro.response.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.TicketDetail;
import com.sankore.astro.pojo.TicketMessage;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 21/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDetailsResponse extends BaseResponse {

    private TicketDetail ticketDetail;

    private List<TicketMessage> messages;

    public TicketDetailsResponse() {}

    public TicketDetailsResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
