package com.sankore.astro.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sankore.astro.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Obi on 10/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {

    private int responseCode;

    private String responseMessage;

    public BaseResponse(){}

    public BaseResponse(int responseCode, String responseMessage){
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public BaseResponse(ResponseCode responseCode){
        this.responseCode = responseCode.getCode();
        this.responseMessage = responseCode.getMessage();
    }
}
