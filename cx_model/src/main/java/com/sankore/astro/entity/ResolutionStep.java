package com.sankore.astro.entity;

import com.sankore.astro.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Obi on 21/05/2019
 */
@Entity
@Getter
@Setter
@Table(name = "resolution_steps")
public class ResolutionStep extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", nullable = false)
    private User updatedBy;

    @Column(name="update_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate = new Date();

    @Column(name="comment", length = 1000)
    private String comment;

    @Column(name="from_client", nullable = false)
    private boolean fromClient = false;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Ticket.class)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name="status", nullable = false)
    private TicketStatus status;

    @Column(name="send_to_client", nullable = false)
    private boolean sendToClient = false;
}
