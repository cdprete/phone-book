package com.cdprete.phonebook.idp.persistence.repository;

import com.cdprete.phonebook.idp.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(@NotBlank String username);

    boolean existsByUsername(@NotBlank String username);
}
