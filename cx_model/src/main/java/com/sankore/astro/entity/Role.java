package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Obi on 15/05/2019
 */
@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name="is_system")
    private boolean system = false;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="client_fk")
    private Client client;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Menu.class)
    @JoinTable(name="role_menu_mapping",
            joinColumns =@JoinColumn(name="role_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="menu_id", referencedColumnName="id"))
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Permission.class)
    @JoinColumn(name="role_fk")
    private Set<Permission> permissions = new HashSet<>();

    @Override
    public boolean equals(Object object) {
        return (object instanceof Role && ((Role) object).getId().equals(this.getId()));
    }
}
