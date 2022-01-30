package com.cdprete.phonebook.utils.mapping;

import com.cdprete.phonebook.api.dto.BasicContactRead;
import com.cdprete.phonebook.persistence.entities.ContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

import javax.validation.constraints.NotNull;

/**
 * @author Cosimo Damiano Prete
 * @since 03/02/2022
 */
@Mapper(config = MappingConfig.class)
interface ContactEntityToBasicContactReadMapper extends Converter<ContactEntity, BasicContactRead> {
    @Mapping(source = "externalId", target = "id")
    BasicContactRead convert(@NotNull ContactEntity entity);
}
