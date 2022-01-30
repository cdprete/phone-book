package com.cdprete.phonebook.api.webservice;

import com.cdprete.phonebook.api.dto.BasicContactRead;
import com.cdprete.phonebook.api.dto.ContactCreate;
import com.cdprete.phonebook.api.dto.ContactRead;
import com.cdprete.phonebook.api.dto.ContactUpdate;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Set;

import static com.cdprete.phonebook.utils.Constants.API_VERSION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * @author Cosimo Damiano Prete
 * @since 05/02/2022
 */
@RequestMapping(API_VERSION + "/contacts")
public interface ContactWebService {
    @NotNull
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Set<@Valid BasicContactRead>> readContacts(@NotNull Pageable pageable,
                                                              @RequestParam(value = "q", required = false) String searchValue,
                                                              @NotNull HttpServletRequest httpServletRequest);

    @Valid
    @NotNull
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    ContactRead readSingleContact(@PathVariable @NotBlank String id);

    @ResponseStatus(CREATED)
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> createContact(@RequestPart @Valid ContactCreate data,
                                       @RequestPart(required = false) MultipartFile image);

    @ResponseStatus(NO_CONTENT)
    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    void updateContact(@PathVariable @NotBlank String id, @RequestPart @Valid ContactUpdate data,
                       @RequestPart(required = false) MultipartFile image) throws IOException;

    @DeleteMapping(value = "/{id}")
    void deleteContact(@PathVariable @NotBlank String id);
}
