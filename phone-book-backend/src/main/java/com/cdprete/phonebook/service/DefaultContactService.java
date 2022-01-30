package com.cdprete.phonebook.service;

import com.cdprete.phonebook.api.dto.BasicContactRead;
import com.cdprete.phonebook.api.dto.ContactCreate;
import com.cdprete.phonebook.api.dto.ContactRead;
import com.cdprete.phonebook.api.dto.ContactUpdate;
import com.cdprete.phonebook.api.dto.EmailAddress;
import com.cdprete.phonebook.api.dto.PhoneNumber;
import com.cdprete.phonebook.api.security.UserInfo;
import com.cdprete.phonebook.api.service.ContactService;
import com.cdprete.phonebook.exceptions.ContactNotFoundException;
import com.cdprete.phonebook.exceptions.DuplicateEmailAddressesException;
import com.cdprete.phonebook.exceptions.DuplicatePhoneNumbersException;
import com.cdprete.phonebook.exceptions.MissingNameAndSurnameException;
import com.cdprete.phonebook.exceptions.NotAnImageException;
import com.cdprete.phonebook.persistence.entities.ContactEntity;
import com.cdprete.phonebook.persistence.repository.ContactRepository;
import com.cdprete.phonebook.utils.mapping.ContactUpdateToContactEntityMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author Cosimo Damiano Prete
 * @since 31/01/2022
 */
@Service
@Scope(proxyMode = INTERFACES)
@Transactional(readOnly = true)
public class DefaultContactService implements ContactService {
    private static final MediaType IMAGE_ALL_MEDIA_TYPE = parseMediaType("image/*");

    private final UserInfo loggedInUser;
    private final ContactRepository repository;
    private final ConversionService conversionService;
    private final ContactUpdateToContactEntityMapper contactUpdateMapper;

    public DefaultContactService(UserInfo loggedInUser,
                                 ContactRepository repository,
                                 ConversionService conversionService,
                                 ContactUpdateToContactEntityMapper contactUpdateMapper) {
        this.loggedInUser = loggedInUser;
        this.repository = repository;
        this.conversionService = conversionService;
        this.contactUpdateMapper = contactUpdateMapper;
    }

    @Override
    public Page<BasicContactRead> readContacts(Pageable pageable, String searchValue) {
        var page = hasText(searchValue) ?
                repository.findAllByCreatorAndNameLikeOrSurnameLikeOrderByCreationDateTime(loggedInUser.getUsername(), searchValue, searchValue, pageable) :
                repository.findAllByCreatorOrderByCreationDateTime(loggedInUser.getUsername(), pageable);

        return page.map(c -> conversionService.convert(c, BasicContactRead.class));
    }

    @Override
    public ContactRead readSingleContact(String id) {
        return repository
                .findByCreatorAndExternalId(loggedInUser.getUsername(), id)
                .map(c -> conversionService.convert(c, ContactRead.class))
                .orElseThrow(() -> new ContactNotFoundException(id));
    }

    @Override
    @Transactional
    public String createContact(ContactCreate contactData, Image image) {
        ensurePartialNameIsAvailable(contactData.getName(), contactData.getSurname());
        ensureNoDuplicateEmailAddresses(contactData.getEmailAddresses());
        ensureNoDuplicatePhoneNumbers(contactData.getPhoneNumbers());

        var entity = conversionService.convert(contactData, ContactEntity.class);
        Assert.notNull(entity, "The converted entity cannot be null");
        setImageToEntity(image, entity);

        return repository.save(entity).getExternalId();
    }

    @Override
    @Transactional
    public String createContact(ContactCreate contactData) {
        return createContact(contactData, null);
    }

    @Override
    @Transactional
    public void updateContact(String id, ContactUpdate updatedContactData, Image image) {
        ensurePartialNameIsAvailable(updatedContactData.getName(), updatedContactData.getSurname());
        ensureNoDuplicateEmailAddresses(updatedContactData.getEmailAddresses());
        ensureNoDuplicatePhoneNumbers(updatedContactData.getPhoneNumbers());

        var entity = repository.findByCreatorAndExternalId(loggedInUser.getUsername(), id).orElseThrow(() -> new ContactNotFoundException(id));
        contactUpdateMapper.updateContactEntityFromDto(updatedContactData, entity);
        setImageToEntity(image, entity);

        repository.save(entity);
    }

    @Override
    @Transactional
    public void updateContact(String id, ContactUpdate updatedContactData) {
        updateContact(id, updatedContactData, null);
    }

    @Override
    @Transactional
    public void deleteContact(String id) {
        repository.deleteByCreatorAndExternalId(loggedInUser.getUsername(), id);
    }

    private static void ensureNoDuplicatePhoneNumbers(List<PhoneNumber> phoneNumbers) {
        ensureNoDuplicateItems(phoneNumbers, PhoneNumber::getPhoneNumber, DuplicatePhoneNumbersException::new);
    }

    private static void ensureNoDuplicateEmailAddresses(List<EmailAddress> emailAddresses) {
        ensureNoDuplicateItems(emailAddresses, EmailAddress::getEmailAddress, DuplicateEmailAddressesException::new);
    }

    private static <T, E extends RuntimeException> void ensureNoDuplicateItems(List<T> items, Function<T, String> mapper, Function<Set<String>, E> throwable) {
        if(!isEmpty(items)) {
            var distinctItems = new HashSet<String>();
            var duplicates = items.stream().map(mapper).filter(p -> !distinctItems.add(p)).collect(toSet());
            if(!duplicates.isEmpty()) {
                throw throwable.apply(duplicates);
            }
        }
    }

    private static void ensureDataMediaTypeIsImage(String mediaType) {
        if(!IMAGE_ALL_MEDIA_TYPE.isCompatibleWith(parseMediaType(mediaType))) {
            throw new NotAnImageException(mediaType);
        }
    }

    private static void ensurePartialNameIsAvailable(String name, String surname) {
        if(!hasText(name) && !hasText(surname)) {
            throw new MissingNameAndSurnameException();
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
