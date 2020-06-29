package de.symeda.sormas.backend.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class GeocodingServiceSirenFrench {
    @EJB
    private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean isEnabled() {
        return configFacade.getGeocodingOsgtsEndpoint() != null;
    }


    public List<String> getSireneEntrepriseAutoComplete(String query) {

        String endpoint = "https://api.insee.fr/entreprises/sirene/siren/";
        if (endpoint == null) {
            return null;
        }

        return getName(query, endpoint);
    }

    List<String> getName(String query, String endpoint) {

        Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        URI url;

        try {
            URIBuilder ub = new URIBuilder(endpoint);
            ub.addParameter("q", "denominationUniteLegale:"+query);
            ub.addParameter("limit", "10");

            url = ub.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }


        WebTarget target = client.target(url);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + "c5173994-3163-3bcf-a39c-7afc0d8dd187")
                .get();

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            if (logger.isErrorEnabled()) {
                logger.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), readAsText(response));
            }
            return null;
        }

        SirenCollection fc = response.readEntity(SirenCollection.class);
        List<UniteLegale> uniteLegales = Optional.of(fc)
                .map(SirenCollection::getUniteLegale)
                .filter(ArrayUtils::isNotEmpty)
                .map(a -> a[0])
                .map(g -> Arrays.asList(g))
                .orElse(null);

        List<PeriodeUniteLegale> listePeriodeUnitesLegales = uniteLegales.stream()
                .map(UniteLegale::getPeriodeUniteLegales)
                .findFirst().get();
        return listePeriodeUnitesLegales.stream()
                .map(PeriodeUniteLegale::getDenominationUniteLegale)
                .collect(Collectors.toList());
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
    public static class SirenCollection implements Serializable {
        private static final long serialVersionUID = -1;
        public String type;
        private UniteLegale[]  uniteLegale;

        @Override
        public String toString() {
            return "type " + type + "\n" + ArrayUtils.toString(getUniteLegale());
        }

        public UniteLegale[]  getUniteLegale() {
            return uniteLegale;
        }

        public void setUniteLegale(UniteLegale[]   uniteLegale) {
            this.uniteLegale = uniteLegale;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UniteLegale implements Serializable {
        private static final long serialVersionUID = -1;
        private List<PeriodeUniteLegale> periodeUniteLegales;
        private String siren;
        private String statutDiffusionUniteLegale;
        private String dateCreationUniteLegale;
        private String sigleUniteLegale;
        private String trancheEffectifsUniteLegale;
        private String anneeEffectifsUniteLegale;
        private String dateDernierTraitementUniteLegale;
        private int nombrePeriodesUniteLegale;
        private String categorieEntreprise;
        private String anneeCategorieEntreprise;

        @Override
        public String toString() {
            return "properties " + periodeUniteLegales;
        }

        public List<PeriodeUniteLegale> getPeriodeUniteLegales() {
            return periodeUniteLegales;
        }

        public void setPeriodeUniteLegales(List<PeriodeUniteLegale> periodeUniteLegales) {
            this.periodeUniteLegales = periodeUniteLegales;
        }

        public String getSiren() {
            return siren;
        }

        public void setSiren(String siren) {
            this.siren = siren;
        }

        public String getStatutDiffusionUniteLegale() {
            return statutDiffusionUniteLegale;
        }

        public void setStatutDiffusionUniteLegale(String statutDiffusionUniteLegale) {
            this.statutDiffusionUniteLegale = statutDiffusionUniteLegale;
        }

        public String getDateCreationUniteLegale() {
            return dateCreationUniteLegale;
        }

        public void setDateCreationUniteLegale(String dateCreationUniteLegale) {
            this.dateCreationUniteLegale = dateCreationUniteLegale;
        }

        public String getSigleUniteLegale() {
            return sigleUniteLegale;
        }

        public void setSigleUniteLegale(String sigleUniteLegale) {
            this.sigleUniteLegale = sigleUniteLegale;
        }

        public String getTrancheEffectifsUniteLegale() {
            return trancheEffectifsUniteLegale;
        }

        public void setTrancheEffectifsUniteLegale(String trancheEffectifsUniteLegale) {
            this.trancheEffectifsUniteLegale = trancheEffectifsUniteLegale;
        }

        public String getAnneeEffectifsUniteLegale() {
            return anneeEffectifsUniteLegale;
        }

        public void setAnneeEffectifsUniteLegale(String anneeEffectifsUniteLegale) {
            this.anneeEffectifsUniteLegale = anneeEffectifsUniteLegale;
        }

        public String getDateDernierTraitementUniteLegale() {
            return dateDernierTraitementUniteLegale;
        }

        public void setDateDernierTraitementUniteLegale(String dateDernierTraitementUniteLegale) {
            this.dateDernierTraitementUniteLegale = dateDernierTraitementUniteLegale;
        }

        public int getNombrePeriodesUniteLegale() {
            return nombrePeriodesUniteLegale;
        }

        public void setNombrePeriodesUniteLegale(int nombrePeriodesUniteLegale) {
            this.nombrePeriodesUniteLegale = nombrePeriodesUniteLegale;
        }

        public String getCategorieEntreprise() {
            return categorieEntreprise;
        }

        public void setCategorieEntreprise(String categorieEntreprise) {
            this.categorieEntreprise = categorieEntreprise;
        }

        public String getAnneeCategorieEntreprise() {
            return anneeCategorieEntreprise;
        }

        public void setAnneeCategorieEntreprise(String anneeCategorieEntreprise) {
            this.anneeCategorieEntreprise = anneeCategorieEntreprise;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeriodeUniteLegale implements Serializable {
        private static final long serialVersionUID = -1;

        private String dateFin;
        private String  dateDebut;
        private String  etatAdministratifUniteLegale;
        private boolean changementEtatAdministratifUniteLegale; 
        private String nomUniteLegale; 
        private boolean changementNomUniteLegale; 
        private String nomUsageUniteLegale; 
        private boolean changementNomUsageUniteLegale; 
        private String denominationUniteLegale;
        private boolean  changementDenominationUniteLegale; 
        private String  denominationUsuelle1UniteLegale; 
        private String denominationUsuelle2UniteLegale; 
        private String denominationUsuelle3UniteLegale; 
        private boolean changementDenominationUsuelleUniteLegale; 
        private String  categorieJuridiqueUniteLegale;
        private boolean changementCategorieJuridiqueUniteLegale; 
        private String activitePrincipaleUniteLegale; 
        private String nomenclatureActivitePrincipaleUniteLegale;
        private boolean changementActivitePrincipaleUniteLegale; 
        private String nicSiegeUniteLegale; 
        private boolean changementNicSiegeUniteLegale;
        private String economieSocialeSolidaireUniteLegale; 
        private boolean  changementEconomieSocialeSolidaireUniteLegale; 
        private String  caractereEmployeurUniteLegale; 
        private boolean  changementCaractereEmployeurUniteLegale;

        public String getDateFin() {
            return dateFin;
        }

        public void setDateFin(String dateFin) {
            this.dateFin = dateFin;
        }

        public String getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(String dateDebut) {
            this.dateDebut = dateDebut;
        }

        public String getEtatAdministratifUniteLegale() {
            return etatAdministratifUniteLegale;
        }

        public void setEtatAdministratifUniteLegale(String etatAdministratifUniteLegale) {
            this.etatAdministratifUniteLegale = etatAdministratifUniteLegale;
        }

        public boolean isChangementEtatAdministratifUniteLegale() {
            return changementEtatAdministratifUniteLegale;
        }

        public void setChangementEtatAdministratifUniteLegale(boolean changementEtatAdministratifUniteLegale) {
            this.changementEtatAdministratifUniteLegale = changementEtatAdministratifUniteLegale;
        }

        public String getNomUniteLegale() {
            return nomUniteLegale;
        }

        public void setNomUniteLegale(String nomUniteLegale) {
            this.nomUniteLegale = nomUniteLegale;
        }

        public boolean isChangementNomUniteLegale() {
            return changementNomUniteLegale;
        }

        public void setChangementNomUniteLegale(boolean changementNomUniteLegale) {
            this.changementNomUniteLegale = changementNomUniteLegale;
        }

        public String getNomUsageUniteLegale() {
            return nomUsageUniteLegale;
        }

        public void setNomUsageUniteLegale(String nomUsageUniteLegale) {
            this.nomUsageUniteLegale = nomUsageUniteLegale;
        }

        public boolean isChangementNomUsageUniteLegale() {
            return changementNomUsageUniteLegale;
        }

        public void setChangementNomUsageUniteLegale(boolean changementNomUsageUniteLegale) {
            this.changementNomUsageUniteLegale = changementNomUsageUniteLegale;
        }

        public String getDenominationUniteLegale() {
            return denominationUniteLegale;
        }

        public void setDenominationUniteLegale(String denominationUniteLegale) {
            this.denominationUniteLegale = denominationUniteLegale;
        }

        public boolean isChangementDenominationUniteLegale() {
            return changementDenominationUniteLegale;
        }

        public void setChangementDenominationUniteLegale(boolean changementDenominationUniteLegale) {
            this.changementDenominationUniteLegale = changementDenominationUniteLegale;
        }

        public String getDenominationUsuelle1UniteLegale() {
            return denominationUsuelle1UniteLegale;
        }

        public void setDenominationUsuelle1UniteLegale(String denominationUsuelle1UniteLegale) {
            this.denominationUsuelle1UniteLegale = denominationUsuelle1UniteLegale;
        }

        public String getDenominationUsuelle2UniteLegale() {
            return denominationUsuelle2UniteLegale;
        }

        public void setDenominationUsuelle2UniteLegale(String denominationUsuelle2UniteLegale) {
            this.denominationUsuelle2UniteLegale = denominationUsuelle2UniteLegale;
        }

        public String getDenominationUsuelle3UniteLegale() {
            return denominationUsuelle3UniteLegale;
        }

        public void setDenominationUsuelle3UniteLegale(String denominationUsuelle3UniteLegale) {
            this.denominationUsuelle3UniteLegale = denominationUsuelle3UniteLegale;
        }

        public boolean isChangementDenominationUsuelleUniteLegale() {
            return changementDenominationUsuelleUniteLegale;
        }

        public void setChangementDenominationUsuelleUniteLegale(boolean changementDenominationUsuelleUniteLegale) {
            this.changementDenominationUsuelleUniteLegale = changementDenominationUsuelleUniteLegale;
        }

        public String getCategorieJuridiqueUniteLegale() {
            return categorieJuridiqueUniteLegale;
        }

        public void setCategorieJuridiqueUniteLegale(String categorieJuridiqueUniteLegale) {
            this.categorieJuridiqueUniteLegale = categorieJuridiqueUniteLegale;
        }

        public boolean isChangementCategorieJuridiqueUniteLegale() {
            return changementCategorieJuridiqueUniteLegale;
        }

        public void setChangementCategorieJuridiqueUniteLegale(boolean changementCategorieJuridiqueUniteLegale) {
            this.changementCategorieJuridiqueUniteLegale = changementCategorieJuridiqueUniteLegale;
        }

        public String getActivitePrincipaleUniteLegale() {
            return activitePrincipaleUniteLegale;
        }

        public void setActivitePrincipaleUniteLegale(String activitePrincipaleUniteLegale) {
            this.activitePrincipaleUniteLegale = activitePrincipaleUniteLegale;
        }

        public String getNomenclatureActivitePrincipaleUniteLegale() {
            return nomenclatureActivitePrincipaleUniteLegale;
        }

        public void setNomenclatureActivitePrincipaleUniteLegale(String nomenclatureActivitePrincipaleUniteLegale) {
            this.nomenclatureActivitePrincipaleUniteLegale = nomenclatureActivitePrincipaleUniteLegale;
        }

        public boolean isChangementActivitePrincipaleUniteLegale() {
            return changementActivitePrincipaleUniteLegale;
        }

        public void setChangementActivitePrincipaleUniteLegale(boolean changementActivitePrincipaleUniteLegale) {
            this.changementActivitePrincipaleUniteLegale = changementActivitePrincipaleUniteLegale;
        }

        public String getNicSiegeUniteLegale() {
            return nicSiegeUniteLegale;
        }

        public void setNicSiegeUniteLegale(String nicSiegeUniteLegale) {
            this.nicSiegeUniteLegale = nicSiegeUniteLegale;
        }

        public boolean isChangementNicSiegeUniteLegale() {
            return changementNicSiegeUniteLegale;
        }

        public void setChangementNicSiegeUniteLegale(boolean changementNicSiegeUniteLegale) {
            this.changementNicSiegeUniteLegale = changementNicSiegeUniteLegale;
        }

        public String getEconomieSocialeSolidaireUniteLegale() {
            return economieSocialeSolidaireUniteLegale;
        }

        public void setEconomieSocialeSolidaireUniteLegale(String economieSocialeSolidaireUniteLegale) {
            this.economieSocialeSolidaireUniteLegale = economieSocialeSolidaireUniteLegale;
        }

        public boolean isChangementEconomieSocialeSolidaireUniteLegale() {
            return changementEconomieSocialeSolidaireUniteLegale;
        }

        public void setChangementEconomieSocialeSolidaireUniteLegale(boolean changementEconomieSocialeSolidaireUniteLegale) {
            this.changementEconomieSocialeSolidaireUniteLegale = changementEconomieSocialeSolidaireUniteLegale;
        }

        public String getCaractereEmployeurUniteLegale() {
            return caractereEmployeurUniteLegale;
        }

        public void setCaractereEmployeurUniteLegale(String caractereEmployeurUniteLegale) {
            this.caractereEmployeurUniteLegale = caractereEmployeurUniteLegale;
        }

        public boolean isChangementCaractereEmployeurUniteLegale() {
            return changementCaractereEmployeurUniteLegale;
        }

        public void setChangementCaractereEmployeurUniteLegale(boolean changementCaractereEmployeurUniteLegale) {
            this.changementCaractereEmployeurUniteLegale = changementCaractereEmployeurUniteLegale;
        }
    }
}
