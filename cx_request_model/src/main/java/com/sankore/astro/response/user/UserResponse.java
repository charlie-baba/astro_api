package com.sankore.astro.response.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 15/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse extends BaseResponse {

    private UserDetail userDetail;

    private List<Menu> menus;

    public UserResponse() { }

    public UserResponse(int responseCode, String responseMessage) {
        this.setResponseCode(responseCode);
        this.setResponseMessage(responseMessage);
    }

    public UserResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
