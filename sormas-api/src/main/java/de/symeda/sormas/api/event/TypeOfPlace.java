/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.event;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum TypeOfPlace {

	FESTIVITIES(new Locale("en")),
	HOME(new Locale("en")),
	HOSPITAL(new Locale("en")),
	MEANS_OF_TRANSPORT(new Locale("en")),
	PUBLIC_PLACE(new Locale("en")),
	UNKNOWN(new Locale("en")),
	SCHOOLS_UNIVERSITIES(new Locale("fr", "FR")),
	CRECHES(new Locale("fr", "FR")),
	EHPAD(new Locale("fr", "FR")),
	MSE_DP(new Locale("fr", "FR")),
	CHILD_WELFARE(new Locale("fr", "FR")),
	SOCIAL_ESTABLISHMENTS(new Locale("fr", "FR")),
	RESIDENTIAL_CARE_HOMELESS_PEOPLE(new Locale("fr", "FR")),
	PENITENTIARY_ESTABLISHMENTS(new Locale("fr", "FR")),
	EXTENDED_FAMILY_ENVIRONMENT(new Locale("fr", "FR")),
	PROFESSIONAL_CIRCLES(new Locale("fr", "FR")),
	EVENT(new Locale("fr", "FR")),
	SMALL_GEOGRAPHIC_UNIT(new Locale("fr", "FR")),
	SPECIFIC_COMMUNITIES(new Locale("fr", "FR")),
	OTHER(new Locale("en"));

	private Locale locale;

	TypeOfPlace(Locale locale) {
		this.locale = locale;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
