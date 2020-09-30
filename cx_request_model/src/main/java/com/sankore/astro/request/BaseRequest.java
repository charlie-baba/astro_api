package com.sankore.astro.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Obi on 10/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRequest implements Serializable {

    @NotEmpty(message = "request content class cannot be empty")
    private String contentClass;

    @NotEmpty(message = "request handler method cannot be empty")
    private RequestMethod handlerMethod;

    private String clientCode;

    private Long userId;

    public List<String> activeUsers;
}
