package com.sankore.astro.entity;

import com.sankore.astro.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Obi on 18/04/2019
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

	private static final long serialVersionUID = -6898997234280105990L;

    @Column(name="sso_id")
    private String ssoId;

    @Column(name="glia_user_id")
    private Long gliaUserId;

	@Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @Column(name = "password")
    private String password;

    @Column(name = "reset_password")
    private boolean resetPassword = false;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = new Date();

    @Column(name = "is_account_blocked")
    private boolean accountBlocked = false;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Client.class)
    @JoinColumn(name = "client_fk")
    private Client client;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Role.class)
    @JoinTable(name = "user_role_mapping",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> userRoles = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Permission.class)
    @JoinColumn(name="user_fk")
    private Set<Permission> customPermissions = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = UserGroupMapping.class)
    @JoinColumn(name="user_fk")
    private Set<UserGroupMapping> userGroupMapping = new HashSet<>();

    public void addRole(Role role) {
        if (!this.userRoles.contains(role)) {
            this.userRoles.add(role);
        }
    }

    public boolean hasRole(String roleName) {
        return this.userRoles.stream().anyMatch(x -> x.getName().equals(roleName));
    }

    public boolean isSuperAdmin() {
        return hasRole(RoleType.SUPER_ADMIN.getScreenName());
    }

    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN.getScreenName());
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof User && ((User) object).getId().equals(this.getId()));
    }
}
