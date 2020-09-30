package com.sankore.astro.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Obi on 22/05/2019
 */
public enum DefaultCommand {

    View(1, "DASHBOARD, CLIENTS, ISSUE_CATEGORY, TICKETS, USERS, ROLES, SLA_SETUP, CLIENT_EMAILS, GROUPS"),
    Update(2, "CLIENTS, ISSUE_CATEGORY, TICKETS, USERS, ROLES, SLA_SETUP, CLIENT_EMAILS, GROUPS"),
    Create(3, "CLIENTS, ISSUE_CATEGORY, TICKETS, USERS, ROLES, SLA_SETUP, CLIENT_EMAILS, GROUPS"),
    Delete(4, "CLIENTS, ISSUE_CATEGORY, USERS, ROLES, SLA_SETUP, CLIENT_EMAILS, GROUPS"),
    Assign(5, "TICKETS"),
    Approve(6, ""),
    Export(7, "TICKETS"),
    Search(8, "CLIENTS, ISSUE_CATEGORY, TICKETS, USERS, ROLES, SLA_SETUP, CLIENT_EMAILS, GROUPS"),
    Reopen(9, "TICKETS")
    ;

    @Getter
    private int position;
    private String menus;

    DefaultCommand(int position, String menus) {
        this.position = position;
        this.menus = menus;
    }

    public static List<DefaultCommand> findByMenu(String menu) {
        return Arrays.stream(values()).filter(c -> c.menus.contains(menu)).collect(Collectors.toList());
    }
}
