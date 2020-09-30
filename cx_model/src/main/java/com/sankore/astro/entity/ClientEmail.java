package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Obi on 28/05/2019
 */
@Entity
@Getter
@Setter
@Table(name = "client_emails")
public class ClientEmail extends BaseEntity {

    private String emailAddress;

    private String password;

    private String server;

    private int port;

    private String protocol;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Client.class)
    @JoinColumn(name = "client_fk")
    private Client client;

    @Override
    public boolean equals(Object object) {
        return (object instanceof ClientEmail && ((ClientEmail) object).getId().equals(this.getId()));
    }
}
