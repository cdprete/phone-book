package com.cdprete.phonebook.webservice;

import com.cdprete.phonebook.api.dto.BasicContactRead;
import com.cdprete.phonebook.api.dto.ContactCreate;
import com.cdprete.phonebook.api.dto.ContactRead;
import com.cdprete.phonebook.api.dto.ContactUpdate;
import com.cdprete.phonebook.api.service.ContactService;
import com.cdprete.phonebook.api.service.ContactService.Image;
import com.cdprete.phonebook.api.webservice.ContactWebService;
import com.cdprete.phonebook.utils.links.Link;
import com.cdprete.phonebook.utils.links.LinkFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.cdprete.phonebook.utils.Constants.API_VERSION;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static org.springframework.http.HttpHeaders.LINK;

/**
 * @author Cosimo Damiano Prete
 * @since 05/02/2022
 */
@RestController
public class DefaultContactWebService implements ContactWebService {
    private final LinkFactory linkFactory;
    private final ContactService contactService;
    private final ConversionService conversionService;

    private static final String HTTP_TOTAL_COUNT_HEADER_NAME = "HTTP-Total-Count";

    private static final String BASE_PATH = format("/%s/contacts", API_VERSION);
    private static final String GET_CONTACT_PATH_TEMPLATE = format("%s/%%s", BASE_PATH);

    public DefaultContactWebService(LinkFactory linkFactory, ContactService contactService, ConversionService conversionService) {
        this.linkFactory = linkFactory;
        this.contactService = contactService;
        this.conversionService = conversionService;
    }

    @Override
    public ResponseEntity<Set<@Valid BasicContactRead>> readContacts(Pageable pageable, String searchValue, HttpServletRequest servletRequest) {
        var unsortedPageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        var contactsPage = contactService.readContacts(unsortedPageRequest, searchValue);
        var links = linkFactory.fromPaginatedHttpRequest(contactsPage, servletRequest);

        return ResponseEntity.ok()
                .header(HTTP_TOTAL_COUNT_HEADER_NAME, Long.toString(contactsPage.getTotalElements()))
                .header(LINK, links.stream().map(Link::toString).collect(joining(", ")))
                .body(contactsPage.stream().collect(toCollection(LinkedHashSet::new)));
    }

    @Override
    public ContactRead readSingleContact(String id) {
        return contactService.readSingleContact(id);
    }

    @Override
    public ResponseEntity<Void> createContact(ContactCreate data, MultipartFile image) {
        var id = image.isEmpty() ?
                contactService.createContact(data) :
                contactService.createContact(data, conversionService.convert(image, Image.class));

        return ResponseEntity.created(URI.create(format(GET_CONTACT_PATH_TEMPLATE, id))).build();
    }

    @Override
    public void updateContact(String id, ContactUpdate data, MultipartFile image) {
        if(image.isEmpty()) {
            contactService.updateContact(id, data);
        } else {
            contactService.updateContact(id, data, conversionService.convert(image, Image.class));
        }
    }

    @Override
    public void deleteContact(String id) {
        contactService.deleteContact(id);
    }
}
