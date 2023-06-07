package com.cdprete.phonebook.idp.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
