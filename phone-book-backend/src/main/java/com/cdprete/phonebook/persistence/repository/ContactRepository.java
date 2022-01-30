package com.cdprete.phonebook.persistence.repository;

import com.cdprete.phonebook.persistence.entities.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Cosimo Damiano Prete
 * @since 31/01/2022
 */
@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    Optional<ContactEntity> findByCreatorAndExternalId(String creator, String externalId);

    Page<ContactEntity> findAllByCreatorOrderByCreationDateTime(String creator, Pageable pageable);

    Page<ContactEntity> findAllByCreatorAndNameLikeOrSurnameLikeOrderByCreationDateTime(String creator, String name, String surname, Pageable pageable);

    void deleteByCreatorAndExternalId(String creator, String externalId);
}
