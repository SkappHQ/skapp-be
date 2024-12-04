package com.rootcode.skapp.peopleplanner.payload.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.rootcode.skapp.peopleplanner.type.AccountStatus;
import com.rootcode.skapp.peopleplanner.type.EEO;
import com.rootcode.skapp.peopleplanner.type.EmployeeType;
import com.rootcode.skapp.peopleplanner.type.EmploymentAllocation;
import com.rootcode.skapp.peopleplanner.type.Gender;
import com.rootcode.skapp.peopleplanner.util.deserializer.AccountStatusDeserializer;
import com.rootcode.skapp.peopleplanner.util.deserializer.EeoDeserializer;
import com.rootcode.skapp.peopleplanner.util.deserializer.EmployeeTypeDeserializer;
import com.rootcode.skapp.peopleplanner.util.deserializer.EmploymentAllocationDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class EmployeeUpdateDto {

	private String authPic;

	private String firstName;

	private String lastName;

	private String middleName;

	private String address;

	private String addressLine2;

	private String personalEmail;

	private Gender gender;

	private String phone;

	private String country;

	private String identificationNo;

	private Set<Long> teams;

	private Long primaryManager;

	private Long secondaryManager;

	private Set<Long> informantManagers;

	private LocalDate joinDate;

	private String timeZone;

	private ProbationPeriodDto employeePeriod;

	@JsonDeserialize(using = EmployeeTypeDeserializer.class)
	private EmployeeType employeeType;

	@JsonDeserialize(using = EeoDeserializer.class)
	private EEO eeo;

	@JsonDeserialize(using = AccountStatusDeserializer.class)
	private AccountStatus accountStatus;

	@JsonDeserialize(using = EmploymentAllocationDeserializer.class)
	private EmploymentAllocation employmentAllocation;

	private List<EmployeeEducationDto> employeeEducations;

	private EmployeePersonalInfoDto employeePersonalInfo;

	private List<EmploymentVisaDto> employeeVisas;

	private List<EmployeeFamilyDto> employeeFamilies;

	private List<EmployeeEmergencyDto> employeeEmergency;

	private List<EmployeeProgressionsDto> employeeProgressions;

	private RoleRequestDto userRoles;

}
