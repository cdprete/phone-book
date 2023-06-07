package com.cdprete.phonebook.utils.mapping;

import com.cdprete.phonebook.api.dto.ContactRead;
import com.cdprete.phonebook.persistence.entities.ContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

import jakarta.validation.constraints.NotNull;

/**
 * @author Cosimo Damiano Prete
 * @since 03/02/2022
 */
@Mapper(config = MappingConfig.class)
interface ContactEntityToContactReadMapper extends Converter<ContactEntity, ContactRead> {
    @Mapping(source = "externalId", target = "id")
    ContactRead convert(@NotNull ContactEntity entity);
}
