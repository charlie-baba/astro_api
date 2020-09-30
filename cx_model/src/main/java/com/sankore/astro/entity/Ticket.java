package com.sankore.astro.entity;

import com.sankore.astro.enums.Media;
import com.sankore.astro.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Obi on 18/04/2019
 */
@Entity
@Getter
@Setter
@Table(name = "tickets")
public class Ticket extends BaseEntity {

	private static final long serialVersionUID = 2455085836650933360L;

	@Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "reporter_first_name", nullable = false)
    private String reporterFirstName;

    @Column(name = "reporter_last_name")
    private String reporterLastName;

    @Column(name = "reporter_email", nullable = false)
    private String reporterEmail;

    @Column(name = "reporter_phone_number")
    private String reporterPhoneNumber;

    @Column(name = "reporter_account_number")
    private String reporterAcctNumber;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = IssueCategory.class)
    @JoinColumn(name = "category_fk")
    private IssueCategory category;
    
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = IssueType.class)
    @JoinColumn(name = "issue_type_fk")
    private IssueType issueType;
    
    @Column(name = "media")
    @Enumerated(EnumType.STRING)
    private Media media;

    @Column(name = "comment", length = 1500)
    private String comment;

    @Column(name = "ticket_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus = TicketStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = new Date();

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Client.class)
    @JoinColumn(name = "client_fk")
    private Client client;

    @Column(name = "level_escalated")
    private int levelEscalated = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="ticket")
    private List<ResolutionStep> resolutionSteps;

    @Override
    public boolean equals(Object object) {
        return (object instanceof Ticket && ((Ticket) object).getId().equals(this.getId()));
    }

    public String getAssignedFullName() {
        if (assignedTo == null)
            return "";

        return assignedTo.getFirstName() + " " + assignedTo.getLastName();
    }

}
