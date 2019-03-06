package com.pvil.otuscourse.task01.dao;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DaoUtils {
    /** Find localized resource for current locale.
     *
     * From https://stackoverflow.com/questions/27886490/load-localized-resources/27953622#27953622
     *
     * @param baseName Basename of the resource. May include a path.
     * @param suffix File extension of the resource.
     * @return Localized resource name or null if none was found.
     */
    public static String getLocalizedResourceName(String baseName, String suffix) {
        Locale locale = Locale.getDefault();
        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);
        List<Locale> candidateLocales = control.getCandidateLocales(baseName, locale);

        for (Locale specificLocale : candidateLocales) {
            String bundleName = control.toBundleName(baseName, specificLocale);
            String resourceName = control.toResourceName(bundleName, suffix);

            if (DaoUtils.class.getResource(resourceName) != null) {
                return resourceName;
            }
        }

        return null;
    }
}
