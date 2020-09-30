package com.sankore.astro.request.group;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.pojo.Group;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;


/**
 * @author Obi on 19/03/2020
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupRequest extends BaseRequest {
	
	private static final long serialVersionUID = -3438813363904279933L;
	
	private Group group;

    private List<UserDetail> members = new ArrayList<>();
    
    public GroupRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }

}
