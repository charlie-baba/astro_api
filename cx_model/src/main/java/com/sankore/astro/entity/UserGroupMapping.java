package com.sankore.astro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_group_mapping", uniqueConstraints = @UniqueConstraint(columnNames = { "user_fk", "group_fk" }))
public class UserGroupMapping extends BaseEntity {

	private static final long serialVersionUID = 4751693248519730078L;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_fk", nullable = false)
    private User user;	

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Group.class)
    @JoinColumn(name = "group_fk", nullable = false)
    private Group group;

    @Column(name="is_supervisor")
    private boolean supervisor = false;

    @Override
    public boolean equals(Object object) {
        return (object instanceof UserGroupMapping && ((UserGroupMapping) object).getId().equals(this.getId()));
    }
}
