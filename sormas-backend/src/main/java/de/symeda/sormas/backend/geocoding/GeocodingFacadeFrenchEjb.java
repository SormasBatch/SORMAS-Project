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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.geocoding;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.geocoding.GeocodingFacadeFrench;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.geocoding.GeocodingFacade;
import de.symeda.sormas.api.region.GeoLatLon;

@Stateless(name = "GeocodingFacadeFrench")
public class GeocodingFacadeFrenchEjb implements GeocodingFacadeFrench {

    /**
    Test
     */
    private static final Pattern TO_BE_ESCAPED = Pattern
            .compile(
                    Stream
                            .of(
                                    "&&",
                                    "||",
                                    "+",
                                    "-",
                                    "!",
                                    "(",
                                    ")",
                                    "{",
                                    "}",
                                    "[",
                                    "]",
                                    "^",
                                    "\"",
                                    "~",
                                    "*",
                                    "?",
                                    ":")
                            .map(Pattern::quote)
                            .collect(Collectors.joining("|")));

    @EJB
    private GeocodingServiceFrench geocodingServiceFrench;

    @EJB
    private GeocodingServiceUniversityFrench geocodingServiceUniversityFrench;

    @EJB
    private GeocodingServiceSirenFrench geocodingServiceSirenFrench;

    @Override
    public boolean isEnabled() {
        return geocodingServiceFrench.isEnabled();
    }

    @Override
    public String getLabel(String address) {

        if (StringUtils.isBlank(address)) {
            return null;
        }

        String textValue = join(", ", address.replaceAll("\\s", " "));

        String query = textValue;
//		Stream.of(
//			property("text", textValue),
//			property("ort", city),
//			property("plz", postalCode),
//			property("strasse", address)
//		)
//		.filter(Objects::nonNull)
//		.collect(Collectors.joining(" AND "));

        if (StringUtils.isBlank(query)) {
            return null;
        }

        return geocodingServiceFrench.getLabel(query);
    }

    @Override
    public List<String> getSireneEntrepriseAutoComplete(String address) {

        if (StringUtils.isBlank(address)) {
            return null;
        }

        String textValue = join(", ", address.replaceAll("\\s", " "));

        String query = textValue;
//		Stream.of(
//			property("text", textValue),
//			property("ort", city),
//			property("plz", postalCode),
//			property("strasse", address)
//		)
//		.filter(Objects::nonNull)
//		.collect(Collectors.joining(" AND "));

        if (StringUtils.isBlank(query)) {
            return null;
        }

        return geocodingServiceSirenFrench.getSireneEntrepriseAutoComplete(query);
    }

    private String join(String delimiter, String ... values) {

        String result = Arrays.stream(values)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(delimiter));

        if (StringUtils.isBlank(result)) {
            return null;
        } else {
            return result;
        }
    }

    private String property(String key, String value) {

        if (StringUtils.isBlank(value))  {
            return "";
        }

        return key + ":\"" + escape(value.trim()) + "\" ";
    }

    String escape(String raw) {
        return TO_BE_ESCAPED.matcher(raw.replace("\\", "\\\\")).replaceAll("\\\\$0");
    }

    @Override
    public List<String> getFrenchSchoolAdresses(String prefixe) {
        String textValue = join(", ", prefixe.replaceAll("\\s", " "));

        String query = textValue;
//		Stream.of(
//			property("text", textValue),
//			property("ort", city),
//			property("plz", postalCode),
//			property("strasse", address)
//		)
//		.filter(Objects::nonNull)
//		.collect(Collectors.joining(" AND "));

        if (StringUtils.isBlank(query)) {
            return null;
        }

        return geocodingServiceUniversityFrench.getFrenchSchoolAdresses(query);
    }
}
