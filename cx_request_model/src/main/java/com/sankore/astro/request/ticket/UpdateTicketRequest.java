package com.sankore.astro.request.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 22/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTicketRequest extends BaseRequest {

    private long ticketId;

    private String comment;

    private boolean sendToClient;

    public UpdateTicketRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
