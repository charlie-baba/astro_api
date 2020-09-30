package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Obi on 20/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDetail implements Serializable {

    private long ticketId;

    private String reference;

    private String reporterFirstName;

    private String reporterLastName;

    private String reporterEmail;

    private String reporterPhoneNumber;

    private String accountNumber;

    private String category;

    private String issueType;

    private String comment;

    private String media;

    private String ticketStatus;

    private String assignedToEmail;

    private String assignedToName;

    private Date dueDate;

    private Date dateCreated;
}
