package com.sankore.astro.request.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * @author Obi on 15/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailRequest extends BaseRequest {

    @NotEmpty(message = "email_address field cannot be empty")
    private String email;

    public EmailRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }

    public EmailRequest(String email, String clientCode) {
        this.email = email;
        this.setClientCode(clientCode);
        this.setContentClass(this.getClass().getSimpleName());
    }
}
