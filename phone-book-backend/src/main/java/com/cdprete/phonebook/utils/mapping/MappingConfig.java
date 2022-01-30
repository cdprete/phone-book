package com.cdprete.phonebook.utils.mapping;

import org.mapstruct.MapperConfig;
import org.mapstruct.extensions.spring.SpringMapperConfig;

/**
 * @author Cosimo Damiano Prete
 * @since 03/02/2022
 */

@SpringMapperConfig
@MapperConfig(componentModel = "spring")
interface MappingConfig {}
