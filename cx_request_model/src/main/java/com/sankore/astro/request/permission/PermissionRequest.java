package com.sankore.astro.request.permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.pojo.Role;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author Obi on 27/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionRequest extends BaseRequest {

    private Set<Role> roles;

    public PermissionRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
