package com.sankore.astro.request.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.pojo.EmailDetail;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 30/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientEmailRequest extends BaseRequest {

    private EmailDetail emailDetail;

    public ClientEmailRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }

}
