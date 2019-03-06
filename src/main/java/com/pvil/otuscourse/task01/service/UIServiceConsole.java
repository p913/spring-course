package com.pvil.otuscourse.task01.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Scanner;

@Service
public class UIServiceConsole implements UIService {

    private final Scanner input = new Scanner(System.in);

    @Autowired
    private MessageSource messageSource;

    private Locale locale = Locale.getDefault();

    @Override
    public void showLine(String line) {
        System.out.println(line);
    }

    @Override
    public void showLineLocalized(String bundlePropName, Object[] params) {
        System.out.println(messageSource.getMessage(bundlePropName, params, locale));
    }

    @Override
    public String inputValue(String caption) {
        if (caption != null)
            System.out.print(caption);

        return input.nextLine();
    }

    @Override
    public String inputValueLocalized(String bundlePropName, Object[] params) {
        if (bundlePropName != null)
            System.out.print(messageSource.getMessage(bundlePropName, params, locale));

        return input.nextLine();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
