package com.sankore.astro.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Obi on 22/05/2019
 */
@Getter
public enum DefaultMenu {

    DASHBOARD("Dashboard", 1, "/dashboard", "Super-Admin, Admin"),
    CLIENTS("Clients", 2, "/client", "Super-Admin"),
    ISSUE_CATEGORY("Issue Category", 3, "/issue-category", "Super-Admin"),

    ROLES("Roles", 2, "/roles", "Admin"),
    USERS("Users", 3, "/user", "Admin"),
    TICKETS("Tickets", 4, "/tickets", "Admin"),
    GROUPS("Groups", 5, "/groups", "Admin"),
    CLIENT_EMAILS("Emails", 6, "/emails", "Admin"),
    SLA_SETUP("SLA", 7, "/sla", "Admin")
    ;

    private String displayName;
    private int position;
    private String url;
    private String roleType;

    DefaultMenu(String displayName, int position, String url, String roleType) {
        this.displayName = displayName;
        this.position = position;
        this.url = url;
        this.roleType = roleType;
    }

    public static List<DefaultMenu> findByRole(String roleType) {
        return Arrays.stream(values()).filter(m -> Arrays.asList(m.roleType.split(", ")).contains(roleType)).collect(Collectors.toList());
    }
}
