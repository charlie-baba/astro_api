package com.sankore.server.emailreader.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author Obi on 28/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRequest implements Serializable {

    @NotEmpty(message = "request content class cannot be empty")
    private String contentClass;

    @NotEmpty(message = "request handler method cannot be empty")
    private String handlerMethod;

    private String clientCode;

    private Long userId;
}