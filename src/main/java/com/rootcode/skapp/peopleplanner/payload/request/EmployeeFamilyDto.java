package com.rootcode.skapp.peopleplanner.payload.request;

import com.rootcode.skapp.peopleplanner.type.FamilyRelationship;
import com.rootcode.skapp.peopleplanner.type.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeFamilyDto {

	private Long familyId;

	private String firstName;

	private String lastName;

	private String parentName;

	private Gender gender;

	private LocalDate birthDate;

	private FamilyRelationship familyRelationship;

}
