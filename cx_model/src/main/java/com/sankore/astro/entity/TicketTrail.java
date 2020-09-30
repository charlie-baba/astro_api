package com.sankore.astro.entity;

import com.sankore.astro.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Obi on 06/06/2019
 */
@Entity
@Getter
@Setter
@Table(name = "ticket_trails")
public class TicketTrail extends BaseEntity {

    private String title;

    private String userFirstName;

    private String userLastName;

    private String userEmail;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = new Date();

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Ticket.class)
    @JoinColumn(name = "ticket_fk")
    private Ticket ticket;

    @Override
    public boolean equals(Object object) {
        return (object instanceof TicketTrail && ((TicketTrail) object).getId().equals(this.getId()));
    }
}
