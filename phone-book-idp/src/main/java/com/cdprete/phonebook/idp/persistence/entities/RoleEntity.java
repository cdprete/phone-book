package com.cdprete.phonebook.idp.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@Entity
@Table(name = "ROLES")
public class RoleEntity extends BaseEntity {
    @Id
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "ROLE", nullable = false, unique = true)
    private String role;

    protected RoleEntity() {}

    public RoleEntity(String role) {
        setRole(role);
    }

    public String getRole() {
        return getId();
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getId() {
        return role;
    }
}
