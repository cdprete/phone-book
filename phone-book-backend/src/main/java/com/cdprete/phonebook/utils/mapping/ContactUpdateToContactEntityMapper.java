package com.cdprete.phonebook.utils.mapping;

import com.cdprete.phonebook.api.dto.ContactUpdate;
import com.cdprete.phonebook.persistence.entities.ContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.core.convert.converter.Converter;

import javax.validation.constraints.NotNull;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;

/**
 * @author Cosimo Damiano Prete
 * @since 07/02/2022
 */
@Mapper(config = MappingConfig.class, collectionMappingStrategy = TARGET_IMMUTABLE)
public interface ContactUpdateToContactEntityMapper extends Converter<ContactUpdate, ContactEntity> {
    void updateContactEntityFromDto(ContactUpdate dto, @NotNull @MappingTarget ContactEntity entity);
}
