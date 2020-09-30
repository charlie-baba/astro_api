package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Obi on 07/06/2019
 */
@Entity
@Getter
@Setter
@Table(name = "ticket_sla", uniqueConstraints = @UniqueConstraint(columnNames = { "issue_type_id", "client_fk" }))
public class TicketSLA extends BaseEntity {

	private static final long serialVersionUID = -884178449744560534L;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="issue_type_id")
    private IssueType issueType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="ticketSLA")
    private Set<EscalationLevel> escalationLevels;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="client_fk", nullable = false)
    private Client client;
}
