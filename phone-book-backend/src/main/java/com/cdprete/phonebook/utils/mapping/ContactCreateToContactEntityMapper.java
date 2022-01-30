package com.cdprete.phonebook.utils.mapping;

import com.cdprete.phonebook.api.dto.ContactCreate;
import com.cdprete.phonebook.persistence.entities.ContactEntity;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Cosimo Damiano Prete
 * @since 05/02/2022
 */
@Mapper(config = MappingConfig.class)
interface ContactCreateToContactEntityMapper extends Converter<ContactCreate, ContactEntity> {}
