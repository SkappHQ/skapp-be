package com.rootcode.skapp.peopleplanner.payload.request;

import com.rootcode.skapp.peopleplanner.type.EmergencyRelationship;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeEmergencyDto {

	private Long emergencyId;

	private String name;

	private EmergencyRelationship emergencyRelationship;

	private String contactNo;

	private Boolean isPrimary;

}
