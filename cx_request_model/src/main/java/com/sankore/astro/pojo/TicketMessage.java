package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Obi on 22/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketMessage implements Serializable {

    private String comment;

    private String dateCreated;

    private boolean fromClient;

    private String username;

    private String status;

    private boolean sendToClient;
}
