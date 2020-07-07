package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum NewContactDateType {
    MOST_RELEVANT,
    ONSET,
    REPORT;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}