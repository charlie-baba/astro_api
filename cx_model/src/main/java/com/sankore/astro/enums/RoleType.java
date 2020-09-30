package com.sankore.astro.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Obi on 22/05/2019
 */
public enum RoleType {

    SUPER_ADMIN("Super-Admin"),
    ADMIN("Admin");

    @Getter
    private String screenName;

    RoleType(String screenName){
        this.screenName = screenName;
    }

    public static List<String> screenNames() {
        return Arrays.stream(values()).map(RoleType::getScreenName).collect(Collectors.toList());
    }

    public static List<String> roleNames(List<RoleType> roles) {
        return roles.stream().map(Enum::name).collect(Collectors.toList());
    }
}
