package org.example.practica3.configurations;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class TranslationProvider implements I18NProvider {
    private final MessageSource messageSource;

    public TranslationProvider(MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(new Locale("en"), new Locale("es"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        try {
            return messageSource.getMessage(key, params, locale);
        } catch (NoSuchMessageException e) {
            return "!" + key + "!";
        }
    }
}
