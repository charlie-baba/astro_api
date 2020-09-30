package com.sankore.astro.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author Obi on 18/04/2019
 */
public enum TicketStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved");

    @Getter
    private String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public static TicketStatus fromDisplayName(String displayName) {
        return Arrays.stream(values()).filter(x -> x.displayName.equals(displayName)).findFirst().orElse(null);
    }
}
