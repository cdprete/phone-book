package com.cdprete.phonebook.webservice;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.ValueExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration(proxyBeanMethods = false)
public class WebServiceConfiguration {
    @Bean
    static LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean() {
            @Override
            protected void postProcessConfiguration(jakarta.validation.Configuration<?> configuration) {
                configuration.addValueExtractor(new ResponseEntityValueExtractor());
                super.postProcessConfiguration(configuration);
            }
        };
    }

    private static class ResponseEntityValueExtractor implements ValueExtractor<ResponseEntity<@ExtractedValue ?>> {
        @Override
        public void extractValues(ResponseEntity<?> originalValue, ValueExtractor.ValueReceiver valueReceiver) {
            valueReceiver.value(null, originalValue.getBody());
        }
    }
}
