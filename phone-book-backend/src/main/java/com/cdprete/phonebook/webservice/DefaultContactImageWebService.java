package com.cdprete.phonebook.webservice;

import com.cdprete.phonebook.api.service.ContactImageService;
import com.cdprete.phonebook.api.service.ContactImageService.Image;
import com.cdprete.phonebook.api.webservice.ContactImagesWebService;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Cosimo Damiano Prete
 * @since 17/03/2022
 */
@RestController
public class DefaultContactImageWebService implements ContactImagesWebService {
    private final ContactImageService contactImageService;
    private final ConversionService conversionService;

    public DefaultContactImageWebService(ContactImageService contactImageService,
                                         ConversionService conversionService) {
        this.contactImageService = contactImageService;
        this.conversionService = conversionService;
    }

    @Override
    public void uploadImage(String id, MultipartFile image) {
        contactImageService.uploadContactImage(id, conversionService.convert(image, Image.class));
    }
}
