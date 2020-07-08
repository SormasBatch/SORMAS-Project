package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.visit.VisitResult;

public class CaseFollowUpDto implements Serializable {
	private static final long serialVersionUID = -7782443664670559221L;

	public static final String I18N_PREFIX = "CaseData";
	public static final String UUID = "uuid";
	public static final String PERSON = "person";
	public static final String REPORT_DATE = "reportDate";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";

	private String uuid;
	private PersonReferenceDto person;
	private Date reportDate;
	private Date symptomsOnsetDate;
	private Date followUpUntil;
	private Disease disease;
	private VisitResult[] visitResults;

	private CaseJurisdictionDto jurisdiction;

	//@formatter:off
	public CaseFollowUpDto(String uuid, String personUuid, String personFirstName, String personLastName,
							  Date reportDate, Date symptomsOnsetDate, Date followUpUntil, Disease disease,
							  String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid,
							  String caseCommunityUud, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
	//formatter:on

		this.uuid = uuid;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.reportDate = reportDate;
		this.symptomsOnsetDate = symptomsOnsetDate;
		this.followUpUntil = followUpUntil;
		this.disease = disease;

		jurisdiction = caseReportingUserUuid == null
			? null
			: new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUud,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
	}

	public void initVisitSize(int i) {
		visitResults = new VisitResult[i];
		Arrays.fill(visitResults, VisitResult.NOT_PERFORMED);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public VisitResult[] getVisitResults() {
		return visitResults;
	}

	public void setVisitResults(VisitResult[] visitResults) {
		this.visitResults = visitResults;
	}

	public CaseJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public Date getSymptomsOnsetDate() {
		return symptomsOnsetDate;
	}

	public void setSymptomsOnsetDate(Date symptomsOnsetDate) {
		this.symptomsOnsetDate = symptomsOnsetDate;
	}
}
