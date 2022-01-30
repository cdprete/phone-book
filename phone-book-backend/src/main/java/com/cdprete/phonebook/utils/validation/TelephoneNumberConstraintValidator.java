package com.cdprete.phonebook.utils.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED;
import static java.lang.String.valueOf;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
public class TelephoneNumberConstraintValidator implements ConstraintValidator<TelephoneNumber, CharSequence> {
    private static final Logger logger = LoggerFactory.getLogger(TelephoneNumberConstraintValidator.class);

    private final PhoneNumberUtil phoneNumberUtil;

    TelephoneNumberConstraintValidator(PhoneNumberUtil phoneNumberUtil) {
        this.phoneNumberUtil = phoneNumberUtil;
    }

    public TelephoneNumberConstraintValidator() {
        this(PhoneNumberUtil.getInstance());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        final String trimmedPhoneNumber = valueOf(value).strip();
        try {
            final PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(trimmedPhoneNumber, UNSPECIFIED.name());

            return phoneNumberUtil.isValidNumber(parsedPhoneNumber);
        } catch (NumberParseException e) {
            logger.debug("An error occurred while parsing the phone number {} for validation", trimmedPhoneNumber, e);
            return false;
        }
    }
}
