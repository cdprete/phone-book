package com.cdprete.phonebook.idp.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Set.copyOf;
import static javax.persistence.CascadeType.ALL;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@Entity
@Table(name = "USERS")
public class UserEntity extends BaseEntity {
    @Id
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @ManyToMany(cascade = ALL)
    @JoinTable(name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USERNAME", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "ROLE", nullable = false)
    )
    private Set<RoleEntity> roles = new LinkedHashSet<>();

    @Override
    public String getId() {
        return username;
    }

    public String getUsername() {
        return getId();
    }

    public void setUsername(String id) {
        this.username = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleEntity> getRoles() {
        return isEmpty(roles) ? emptySet() : copyOf(roles);
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles.clear();
        if(!isEmpty(roles)) {
            this.roles.addAll(roles);
        }
    }
}
