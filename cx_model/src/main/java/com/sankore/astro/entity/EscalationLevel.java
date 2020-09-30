package com.sankore.astro.entity;


import com.sankore.astro.enums.SLATimeUnit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Obi on 13/08/2020
 */
@Entity
@Getter
@Setter
@Table(name = "escalation_levels", uniqueConstraints = @UniqueConstraint(columnNames = { "level", "ticket_sla_id" }))
public class EscalationLevel extends BaseEntity {

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "period", nullable = false)
    private int period;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_unit", nullable = false)
    private SLATimeUnit timeUnit;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="ticket_sla_id")
    private TicketSLA ticketSLA;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinTable(name = "escalation_user_mapping",
            joinColumns = { @JoinColumn(name = "escalation_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> admins = new HashSet<>();

}
