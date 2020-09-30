package com.sankore.astro.response.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.pojo.Permission;
import com.sankore.astro.pojo.Role;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 25/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDetailResponse extends BaseResponse {

    private Role roles;

    private List<Menu> menus;

    private List<Permission> permissions;

    public RoleDetailResponse() { }

    public RoleDetailResponse(int code, String message) {
        this.setResponseCode(code);
        this.setResponseMessage(message);
    }

    public RoleDetailResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
