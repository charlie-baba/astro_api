package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Obi on 15/05/2019
 */
@Entity
@Getter
@Setter
@Table(name = "menus")
public class Menu extends BaseEntity {

    private String name;

    @Column(name="is_parent")
    private boolean parent = true;

    private String url;

    private int position;

    private String commands;

    @Override
    public boolean equals(Object object) {
        return (object instanceof Menu && ((Menu) object).getId().equals(this.getId()));
    }
}
