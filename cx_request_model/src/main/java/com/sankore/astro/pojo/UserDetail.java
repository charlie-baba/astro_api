package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Obi on 15/05/2019
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetail implements Serializable {

    private Long id;

    private String ssoId;

    private Long gliaUserId;

    @NotEmpty(message = "first_name field cannot be empty")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "last_name field cannot be empty")
    private String lastName;

    private String avatar;

    private String phoneNumber;

    @NotEmpty(message = "request_channel field cannot be empty")
    private String requestChannel;

    @NotEmpty(message = "email_address field cannot be empty")
    private String email;

    private String password;

    private boolean active = false;
    
    private boolean resetPassword = false;

    private Long clientId;

    private String clientCode;

    private String clientName;

    private String clientLogo;

    private boolean superAdmin;

    private boolean admin;

    private Set<Role> roles;
}
