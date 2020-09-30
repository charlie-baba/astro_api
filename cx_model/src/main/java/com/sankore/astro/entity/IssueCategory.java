package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Obi on 06/06/2019
 */
@Entity
@Getter
@Setter
@Table(name = "issue_categories")
public class IssueCategory extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = IssueType.class)
    @JoinColumn(name="issue_category_fk")
    private Set<IssueType> issueTypes = new HashSet<>();

    @Override
    public boolean equals(Object object) {
        return (object instanceof IssueCategory && ((IssueCategory) object).getId().equals(this.getId()));
    }
}
