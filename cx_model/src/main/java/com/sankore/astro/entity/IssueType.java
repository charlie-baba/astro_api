package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Obi on 06/06/2019
 */
@Entity
@Getter
@Setter
@Table(name = "issue_types")
public class IssueType extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(targetEntity = IssueCategory.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_category_fk", nullable = false)
    private IssueCategory issueCategory;

    @Override
    public boolean equals(Object object) {
        return (object instanceof IssueType && ((IssueType) object).getId()!= null && ((IssueType) object).getId().equals(this.getId()));
    }
}
