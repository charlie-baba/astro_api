package com.sankore.astro.response.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Group;
import com.sankore.astro.response.BaseResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 20/3/2020
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupResponse extends BaseResponse {
	
	private static final long serialVersionUID = 8653980894886802041L;
	
	private Group group;

    public GroupResponse() { }

    public GroupResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
    
    public GroupResponse(int responseCode, String responseMessage) {
        this.setResponseCode(responseCode);
        this.setResponseMessage(responseMessage);
    }

}
