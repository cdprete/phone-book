package com.cdprete.phonebook.utils.mapping;

import com.cdprete.phonebook.api.service.ContactImageService.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author Cosimo Damiano Prete
 * @since 08/02/2022
 */
@Mapper(config = MappingConfig.class)
interface MultipartFileToImageMapper extends Converter<MultipartFile, Image> {
    @Override
    @Mapping(source = "contentType", target = "mediaType")
    @Mapping(expression = "java(getBytesUnchecked(source))", target = "bytes")
    Image convert(@NotNull MultipartFile source);

    default byte[] getBytesUnchecked(MultipartFile source) {
        try {
            return source == null ? null : source.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
