package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Obi on 15/05/2019
 */
@Entity
@Getter
@Setter
@Table(name = "permissions")
public class Permission extends BaseEntity {

    @Column(name="commands")
    private String commands;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Menu.class)
    @JoinColumn(name="menu_fk")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Role.class)
    @JoinColumn(name = "role_fk")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_fk")
    private User user;

    @Override
    public  boolean equals(Object object) {
        return ((object instanceof Permission) && ((Permission)object).getId() != null && ((Permission)object).getId().equals(this.getId()));
    }
}
