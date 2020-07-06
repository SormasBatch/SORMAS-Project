package de.symeda.sormas.backend.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class GeocodingServiceUniversityFrench {
    @EJB
    private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean isEnabled() {
        return configFacade.getGeocodingOsgtsEndpoint() != null;
    }

    public List<String> getFrenchSchoolAdresses(String query) {

        String endpoint = "https://data.education.gouv.fr/api/records/1.0/search/?dataset=fr-en-adresse-et-geolocalisation-etablissements-premier-et-second-degre";
        if (endpoint == null) {
            return null;
        }

        return getAppelationOfficielle(query, endpoint);
    }


    List<String> getAppelationOfficielle(String query, String endpoint) {

        Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        URI url;

        try {
            URIBuilder ub = new URIBuilder(endpoint);
            //ub.addParameter("q", query);
            if (!StringUtils.isBlank(query)) {
                ub.addParameter("q", query);
            }
            ub.addParameter("rows", "500");

            url = ub.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }


        WebTarget target = client.target(url);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            if (logger.isErrorEnabled()) {
                logger.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), readAsText(response));
            }
            return null;
        }

        RecordCollection fc = response.readEntity(RecordCollection.class);
        List<String> returnList = new ArrayList<>();
        List<Record> records = Optional.of(fc)
                .map(RecordCollection::getRecords)
                .filter(ArrayUtils::isNotEmpty)
                .map(g -> Arrays.asList(g))
                .orElse(null);
       if(records != null){
           returnList = records.stream()
                   .map(Record::getFields)
                   .map(RecordProperties::getAppellation_officielle)
                   .collect(Collectors.toList());
       }

        return returnList;
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
    public static class RecordCollection implements Serializable {
        private static final long serialVersionUID = -1;
        private Record[] records;

        @Override
        public String toString() {
            return "records " + "\n" + ArrayUtils.toString(getRecords());
        }

        public Record[] getRecords() {
            return records;
        }

        public void setRecords(Record[] records) {
            this.records = records;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Record implements Serializable {
        private static final long serialVersionUID = -1;
        private RecordProperties fields;
        private String datasetid;
        private String recordid;

        @Override
        public String toString() {
            return "datasetid " + getDatasetid() + "\n recordid " + getRecordid() + "\n fields " + getFields();
        }

        public RecordProperties getFields() {
            return fields;
        }

        public void setFields(RecordProperties fields) {
            this.fields = fields;
        }

        public String getDatasetid() {
            return datasetid;
        }

        public void setDatasetid(String datasetid) {
            this.datasetid = datasetid;
        }

        public String getRecordid() {
            return recordid;
        }

        public void setRecordid(String recordid) {
            this.recordid = recordid;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecordProperties implements Serializable {
        private static final long serialVersionUID = -1;

        private String localite_acheminement_uai;
        private String code_ministere;
        private String secteur_public_prive_libe;
        private int etat_etablissement;
        private String libelle_region;
        private int secteur_prive_code_type_contrat;
        private String code_postal_uai;
        private String numero_uai;
        private String libelle_ministere;
        private String code_region;
        private String epsg;
        private String nature_uai_libe;
        private String appellation_officielle;
        private double latitude;
        private String secteur_prive_libelle_type_contrat;
        private double coordonnee_y;
        private double coordonnee_x;
        private String adresse_uai;
        private String code_commune;
        private String libelle_commune;
        private String etat_etablissement_libe;
        private double longitude;

        public String getLocalite_acheminement_uai() {
            return localite_acheminement_uai;
        }

        public void setLocalite_acheminement_uai(String localite_acheminement_uai) {
            this.localite_acheminement_uai = localite_acheminement_uai;
        }

        public String getCode_ministere() {
            return code_ministere;
        }

        public void setCode_ministere(String code_ministere) {
            this.code_ministere = code_ministere;
        }

        public String getSecteur_public_prive_libe() {
            return secteur_public_prive_libe;
        }

        public void setSecteur_public_prive_libe(String secteur_public_prive_libe) {
            this.secteur_public_prive_libe = secteur_public_prive_libe;
        }

        public int getEtat_etablissement() {
            return etat_etablissement;
        }

        public void setEtat_etablissement(int etat_etablissement) {
            this.etat_etablissement = etat_etablissement;
        }

        public String getLibelle_region() {
            return libelle_region;
        }

        public void setLibelle_region(String libelle_region) {
            this.libelle_region = libelle_region;
        }

        public int getSecteur_prive_code_type_contrat() {
            return secteur_prive_code_type_contrat;
        }

        public void setSecteur_prive_code_type_contrat(int secteur_prive_code_type_contrat) {
            this.secteur_prive_code_type_contrat = secteur_prive_code_type_contrat;
        }

        public String getCode_postal_uai() {
            return code_postal_uai;
        }

        public void setCode_postal_uai(String code_postal_uai) {
            this.code_postal_uai = code_postal_uai;
        }

        public String getNumero_uai() {
            return numero_uai;
        }

        public void setNumero_uai(String numero_uai) {
            this.numero_uai = numero_uai;
        }

        public String getLibelle_ministere() {
            return libelle_ministere;
        }

        public void setLibelle_ministere(String libelle_ministere) {
            this.libelle_ministere = libelle_ministere;
        }

        public String getCode_region() {
            return code_region;
        }

        public void setCode_region(String code_region) {
            this.code_region = code_region;
        }

        public String getEpsg() {
            return epsg;
        }

        public void setEpsg(String epsg) {
            this.epsg = epsg;
        }

        public String getNature_uai_libe() {
            return nature_uai_libe;
        }

        public void setNature_uai_libe(String nature_uai_libe) {
            this.nature_uai_libe = nature_uai_libe;
        }

        public String getAppellation_officielle() {
            return appellation_officielle;
        }

        public void setAppellation_officielle(String appellation_officielle) {
            this.appellation_officielle = appellation_officielle;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getSecteur_prive_libelle_type_contrat() {
            return secteur_prive_libelle_type_contrat;
        }

        public void setSecteur_prive_libelle_type_contrat(String secteur_prive_libelle_type_contrat) {
            this.secteur_prive_libelle_type_contrat = secteur_prive_libelle_type_contrat;
        }

        public double getCoordonnee_y() {
            return coordonnee_y;
        }

        public void setCoordonnee_y(double coordonnee_y) {
            this.coordonnee_y = coordonnee_y;
        }

        public double getCoordonnee_x() {
            return coordonnee_x;
        }

        public void setCoordonnee_x(double coordonnee_x) {
            this.coordonnee_x = coordonnee_x;
        }

        public String getAdresse_uai() {
            return adresse_uai;
        }

        public void setAdresse_uai(String adresse_uai) {
            this.adresse_uai = adresse_uai;
        }

        public String getCode_commune() {
            return code_commune;
        }

        public void setCode_commune(String code_commune) {
            this.code_commune = code_commune;
        }

        public String getLibelle_commune() {
            return libelle_commune;
        }

        public void setLibelle_commune(String libelle_commune) {
            this.libelle_commune = libelle_commune;
        }

        public String getEtat_etablissement_libe() {
            return etat_etablissement_libe;
        }

        public void setEtat_etablissement_libe(String etat_etablissement_libe) {
            this.etat_etablissement_libe = etat_etablissement_libe;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
