package com.sankore.server.emailreader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Obi on 18/04/2019
 */
@Entity
@Getter
@Setter
@Table(name = "clients")
public class Client extends BaseEntity {

    private String name;

    private String email;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "base_url")
    private String baseUrl;

    private String logo;

    private String description;

    @Column(name = "client_code", unique = true)
    private String code;

    private String template;

    @Column(name = "is_system_defined")
    private boolean systemDefined = false;

    @Override
    public boolean equals(Object object) {
        return (object instanceof Client && ((Client) object).getId().equals(this.getId()));
    }
}
