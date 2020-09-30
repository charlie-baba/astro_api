package com.sankore.astro.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Obi on 28/05/2019
 */
@Entity
@Getter
@Setter
@Table(name = "groups", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "client_fk" }))
public class Group extends BaseEntity {

	private static final long serialVersionUID = 6595585195087590069L;

	private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Client.class)
    @JoinColumn(name = "client_fk")
    private Client client;
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = UserGroupMapping.class)
    @JoinColumn(name="group_fk")
    private Set<UserGroupMapping> userGroupMapping = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = IssueCategory.class)
    @JoinTable(name = "group_category_mapping",
            joinColumns = { @JoinColumn(name = "group_id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id") })
    private Set<IssueCategory> issueCategories = new HashSet<>();

    @Override
    public boolean equals(Object object) {
        return (object instanceof Group && ((Group) object).getId().equals(this.getId()));
    }
}
