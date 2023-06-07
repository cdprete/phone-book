package com.cdprete.phonebook.idp.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Set.copyOf;
import static jakarta.persistence.CascadeType.ALL;
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
