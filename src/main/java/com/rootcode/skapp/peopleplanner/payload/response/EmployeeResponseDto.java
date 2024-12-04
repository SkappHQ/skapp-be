package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.type.EmployeeUserRole;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEducationDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEmergencyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeePersonalInfoDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmploymentVisaDto;
import com.rootcode.skapp.peopleplanner.payload.request.JobFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.request.JobTitleDto;
import com.rootcode.skapp.peopleplanner.type.AccountStatus;
import com.rootcode.skapp.peopleplanner.type.EEO;
import com.rootcode.skapp.peopleplanner.type.EmploymentAllocation;
import com.rootcode.skapp.peopleplanner.type.Ethnicity;
import com.rootcode.skapp.peopleplanner.type.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeResponseDto {

	private Long employeeId;

	private String firstName;

	private String lastName;

	private String middleName;

	private String address;

	private String addressLine2;

	private String designation;

	private String authPic;

	private String identificationNo;

	private String email;

	private JobTitleDto jobTitle;

	private JobFamilyDto jobFamilyDto;

	private EmployeePeriodResponseDto probationPeriod;

	private EmploymentAllocation employmentAllocation;

	private AccountStatus accountStatus;

	private String country;

	private String personalEmail;

	private String phone;

	private Gender gender;

	private List<EmployeeProgressionResponseDto> employeeProgressions;

	private EEO eeo;

	private Ethnicity ethnicity;

	private List<TeamEmployeeResponseDto> teams;

	private List<EmploymentVisaDto> employeeVisas;

	private List<EmployeeEmergencyDto> employeeEmergencies;

	private EmployeePersonalInfoDto employeePersonalInfo;

	private List<EmployeeEducationDto> employeeEducations;

	private List<EmployeeFamilyDto> employeeFamilies;

	private EmployeeUserRole userRole;

}
