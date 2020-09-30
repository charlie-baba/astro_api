package com.sankore.astro.request.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 21/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketIdRequest extends BaseRequest {

    private long ticketId;

    private long assignToUserId;

    public TicketIdRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }

}
