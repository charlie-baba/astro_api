package com.sankore.astro.request.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Obi on 12/05/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest extends BaseRequest {

    private Long id;

    private String ssoId;

    private Long gliaUserId;

    private Long clientId;

    @NotEmpty(message = "first_name field cannot be empty")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "last_name field cannot be empty")
    private String lastName;

    private String avatar;

    private String phoneNumber;

    private String password;

    @NotEmpty(message = "email_address field cannot be empty")
    private String email;

    private boolean active = false;

    private List<Long> roleIds = new ArrayList<>();

    private List<Menu> menus = new ArrayList<>();

    public UserRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
