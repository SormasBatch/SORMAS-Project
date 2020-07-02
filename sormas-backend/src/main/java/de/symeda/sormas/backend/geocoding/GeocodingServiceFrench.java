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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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


    public Map<String, Map<String, String>> getAdresses(String query) {

        String endpoint = "http://api-adresse.data.gouv.fr/search/";
        if (endpoint == null) {
            return null;
        }

        return getLabels(query, endpoint);
    }


    Map<String, Map<String, String>> getLabels(String query, String endpoint) {

        Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        URI url;

        try {
            URIBuilder ub = new URIBuilder(endpoint);
            ub.addParameter("q", query);
            ub.addParameter("type", "street");
            ub.addParameter("limit", "50");

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

        List<String> returnList = new ArrayList<>();
        Map<String, Map<String, String>> returnMap = new HashMap<>();

        Map<String, String> map;

        List<Feature> features = Optional.of(fc)
                .map(FeatureCollection::getFeatures)
                .filter(ArrayUtils::isNotEmpty)
                .map(g -> Arrays.asList(g))
                .orElse(null);
        if(features != null) {
            List<String> listNames = features.stream()
                    .map(Feature::getProperties)
                    .map(FeatureProperties::getLabel).collect(Collectors.toList());

            List<String> listPostCodes = features.stream()
                    .map(Feature::getProperties)
                    .map(FeatureProperties::getPostcode).collect(Collectors.toList());

            List<String> listCities = features.stream()
                    .map(Feature::getProperties)
                    .map(FeatureProperties::getCity).collect(Collectors.toList());

            List<Double> listLatitudes = features.stream()
                    .map(Feature::getProperties)
                    .map(FeatureProperties::getX).collect(Collectors.toList());

            List<Double> listLongitudes = features.stream()
                    .map(Feature::getProperties)
                    .map(FeatureProperties::getY).collect(Collectors.toList());

            for (int i = 0; i < listNames.size(); i++) {
                map = new HashMap<>();
                map.put("postCode",listPostCodes.get(i));
                map.put("city",listCities.get(i));
                map.put("latitude",listLatitudes.get(i).toString());
                map.put("longitude",listLongitudes.get(i).toString());
                returnMap.put(listNames.get(i),map);
            }
        }

        return returnMap;
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

        private String label;
        private double score;
        private String id;
        private String type;
        private double x;
        private double y;
        private double importance;
        private String name;
        private String postcode;
        private String citycode;
        private String city;
        private String context;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getImportance() {
            return importance;
        }

        public void setImportance(double importance) {
            this.importance = importance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getCitycode() {
            return citycode;
        }

        public void setCitycode(String citycode) {
            this.citycode = citycode;
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
    }
}