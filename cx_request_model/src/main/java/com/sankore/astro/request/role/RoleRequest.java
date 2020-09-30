package com.sankore.astro.request.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.pojo.Role;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Obi on 25/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleRequest extends BaseRequest {

    private Role role;

    private List<Menu> menus = new ArrayList<>();

    public RoleRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }

}
