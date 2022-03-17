package com.cdprete.phonebook.api.webservice;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

import static com.cdprete.phonebook.utils.Constants.API_VERSION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * @author Cosimo Damiano Prete
 * @since 17/03/2022
 */
@RequestMapping(API_VERSION + "/contacts/{id}/images")
public interface ContactImagesWebService {
    String IMAGE_ALL_VALUE = "image/*";

    @ResponseStatus(NO_CONTENT)
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    void uploadImage(@PathVariable @NotBlank String id, @RequestPart MultipartFile image);
}
