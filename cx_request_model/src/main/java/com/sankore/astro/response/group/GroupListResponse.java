package com.sankore.astro.response.group;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Group;
import com.sankore.astro.response.BaseResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 20/03/2020
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupListResponse extends BaseResponse {

	private static final long serialVersionUID = -5075727805596752662L;
	
	private List<Group> groups;

    public GroupListResponse() { }

    public GroupListResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
