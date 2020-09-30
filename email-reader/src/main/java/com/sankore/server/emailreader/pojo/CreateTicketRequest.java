package com.sankore.server.emailreader.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 28/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTicketRequest extends BaseRequest {

	private static final long serialVersionUID = 9182781854741885106L;

	private String reporterFirstName;

    private String reporterLastName;

    private String reporterEmail;

    private String reporterPhoneNumber;

    private String comment;

    private String media;
    
    private String issueCategory;

    private Long groupId;

    public CreateTicketRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}