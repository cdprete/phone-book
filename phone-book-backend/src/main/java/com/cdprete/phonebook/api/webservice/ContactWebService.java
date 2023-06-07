package com.cdprete.phonebook.api.webservice;

import com.cdprete.phonebook.api.dto.BasicContactRead;
import com.cdprete.phonebook.api.dto.ContactCreate;
import com.cdprete.phonebook.api.dto.ContactRead;
import com.cdprete.phonebook.api.dto.ContactUpdate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> createContact(@RequestBody @Valid ContactCreate data);

    @ResponseStatus(NO_CONTENT)
    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    void updateContact(@PathVariable @NotBlank String id, @RequestPart @Valid ContactUpdate data,
                       @RequestPart(required = false) MultipartFile image);

    @ResponseStatus(NO_CONTENT)
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    void updateContact(@PathVariable @NotBlank String id, @RequestBody @Valid ContactUpdate data);

    @DeleteMapping(value = "/{id}")
    void deleteContact(@PathVariable @NotBlank String id);
}
