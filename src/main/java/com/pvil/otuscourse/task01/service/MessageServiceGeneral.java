package com.pvil.otuscourse.task01.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageServiceGeneral implements MessageService {

    @Autowired
    private MessageSource messageSource;

    private Locale locale = Locale.getDefault();

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getMessageLocalized(String bundlePropName, Object[] params) {
        return messageSource.getMessage(bundlePropName, params, locale);
    }

    @Override
    public String getMessageLocalized(String bundlePropName) {
        return messageSource.getMessage(bundlePropName, null, locale);
    }
}
