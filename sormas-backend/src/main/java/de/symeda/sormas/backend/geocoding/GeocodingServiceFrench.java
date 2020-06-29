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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

/**
 * Récupération label API adresses
 */
@Stateless
@LocalBean
public class GeocodingServiceFrench {

    @EJB
    private ConfigFacadeEjbLocal configFacade;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean isEnabled() {
        return configFacade.getGeocodingOsgtsEndpoint() != null;
    }


    public String getLabel(String query) {

        String endpoint = "http://api-adresse.data.gouv.fr/search/";
        if (endpoint == null) {
            return null;
        }

        return getLabel(query, endpoint);
    }


    String getLabel(String query, String endpoint) {

        Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        URI url;

        try {
            URIBuilder ub = new URIBuilder(endpoint);
            ub.addParameter("q", query);
            ub.addParameter("type", "street");
            ub.addParameter("limit", "10");

            url = ub.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }


        WebTarget target = client.target(url);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
            if (logger.isErrorEnabled()) {
                logger.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), readAsText(response));
            }
            return null;
        }

        FeatureCollection fc = response.readEntity(FeatureCollection.class);

        return Optional.of(fc)
                .map(FeatureCollection::getFeatures)
                .filter(ArrayUtils::isNotEmpty)
                .map(a -> a[0])
                .map(Feature::getProperties)
                .map(FeatureProperties::getLabel)
                .orElse(null);
    }

    String getName(String query, String endpoint) {

        Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        URI url;

        try {
            URIBuilder ub = new URIBuilder(endpoint);
            ub.addParameter("q", query);
            ub.addParameter("limit", "10");

            url = ub.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }


        WebTarget target = client.target(url);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
            if (logger.isErrorEnabled()) {
                logger.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), readAsText(response));
            }
            return null;
        }

        FeatureCollection fc = response.readEntity(FeatureCollection.class);

        return Optional.of(fc)
                .map(FeatureCollection::getFeatures)
                .filter(ArrayUtils::isNotEmpty)
                .map(a -> a[0])
                .map(Feature::getProperties)
                .map(FeatureProperties::getName)
                .orElse(null);
    }


    private String readAsText(Response response) {
        try {
            return response.readEntity(String.class).trim();
        } catch (RuntimeException e) {
            return "(Exception when retrieving body: " + e + ")";
        }
    }

    @XmlRootElement
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeatureCollection implements Serializable {
        private static final long serialVersionUID = -1;
        public String type;
        private Feature[] features;

        @Override
        public String toString() {
            return "type " + type + "\n" + ArrayUtils.toString(getFeatures());
        }

        public Feature[] getFeatures() {
            return features;
        }

        public void setFeatures(Feature[] features) {
            this.features = features;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature implements Serializable {
        private static final long serialVersionUID = -1;
        private FeatureProperties properties;

        @Override
        public String toString() {
            return "properties " + properties;
        }

        public FeatureProperties getProperties() {
            return properties;
        }

        public void setProperties(FeatureProperties properties) {
            this.properties = properties;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeatureProperties implements Serializable {
        private static final long serialVersionUID = -1;

        private String text;
        private String type;
        private double score;
        private String houseNumber;
        private String name;
        private String postCode;
        private String cityCode;
        private String city;
        private String context;
        private String label;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String typ) {
            this.type = type;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getHouseNumber() {
            return houseNumber;
        }

        public void setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String rs) {
            this.name = name;
        }

        public String getPostCode() {
            return postCode;
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

    }
}