package com.sankore.astro.request.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 10/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTicketRequest extends BaseRequest {

    private Long ticketId;

    private String reporterFirstName;

    private String reporterLastName;

    private String reporterEmail;

    private String reporterPhoneNumber;

    private String comment;

    private String media;

    private String accountNumber;

    private Long issueCategoryId;

    private String issueCategory;

    private Long issueTypeId;

    private String issueType;

    private Long groupId;

    public CreateTicketRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}