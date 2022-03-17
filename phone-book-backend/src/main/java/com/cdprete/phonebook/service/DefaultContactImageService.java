package com.cdprete.phonebook.service;

import com.cdprete.phonebook.api.security.UserInfo;
import com.cdprete.phonebook.api.service.ContactImageService;
import com.cdprete.phonebook.exceptions.ContactNotFoundException;
import com.cdprete.phonebook.exceptions.NotAnImageException;
import com.cdprete.phonebook.persistence.entities.ContactEntity;
import com.cdprete.phonebook.persistence.repository.ContactRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cdprete.phonebook.api.webservice.ContactImagesWebService.IMAGE_ALL_VALUE;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;
import static org.springframework.http.MediaType.parseMediaType;

@Service
@Scope(proxyMode = INTERFACES)
@Transactional(readOnly = true)
public class DefaultContactImageService implements ContactImageService {
    private static final MediaType IMAGE_ALL_MEDIA_TYPE = parseMediaType(IMAGE_ALL_VALUE);

    private final UserInfo loggedInUser;
    private final ContactRepository repository;

    public DefaultContactImageService(UserInfo loggedInUser, ContactRepository repository) {
        this.loggedInUser = loggedInUser;
        this.repository = repository;
    }

    @Override
    @Transactional
    public void uploadContactImage(String id, Image image) {
        var entity = repository
                .findByCreatorAndExternalId(loggedInUser.getUsername(), id)
                .orElseThrow(() -> new ContactNotFoundException(id));
        setImageToEntity(image, entity);
        repository.save(entity);
    }

    private static void ensureDataMediaTypeIsImage(String mediaType) {
        if(!IMAGE_ALL_MEDIA_TYPE.isCompatibleWith(parseMediaType(mediaType))) {
            throw new NotAnImageException(mediaType);
        }
    }

    private static void setImageToEntity(Image image, ContactEntity entity) {
        final byte[] imageBytes;
        if(image == null) {
            imageBytes = null;
        } else {
            ensureDataMediaTypeIsImage(image.mediaType());
            imageBytes = image.bytes();
        }
        entity.setImage(imageBytes);
    }
}
