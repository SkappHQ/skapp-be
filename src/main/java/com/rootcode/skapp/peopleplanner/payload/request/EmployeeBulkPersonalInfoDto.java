package com.rootcode.skapp.peopleplanner.payload.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.rootcode.skapp.peopleplanner.type.BloodGroup;
import com.rootcode.skapp.peopleplanner.type.Ethnicity;
import com.rootcode.skapp.peopleplanner.type.MaritalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeBulkPersonalInfoDto {

	private String city;

	private String state;

	private String postalCode;

	private String birthDate;

	private Ethnicity ethnicity;

	private String ssn;

	private String nationality;

	private String nin;

	private MaritalStatus maritalStatus;

	private JsonNode socialMediaDetails;

	private BloodGroup bloodGroup;

	private JsonNode extraInfo;

}
