package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEducationDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEmergencyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeePersonalInfoDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmploymentVisaDto;
import com.rootcode.skapp.peopleplanner.payload.request.JobTitleDto;
import com.rootcode.skapp.peopleplanner.type.AccountStatus;
import com.rootcode.skapp.peopleplanner.type.EEO;
import com.rootcode.skapp.peopleplanner.type.EmployeeType;
import com.rootcode.skapp.peopleplanner.type.EmploymentAllocation;
import com.rootcode.skapp.peopleplanner.type.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ManagerEmployeeDto {

	private String employeeId;

	private String identificationNo;

	private String firstName;

	private String lastName;

	private String middleName;

	private String designation;

	private String authPic;

	private String email;

	private String personalEmail;

	private String address;

	private String addressLine2;

	private String country;

	private AccountStatus accountStatus;

	private EmploymentAllocation employmentAllocation;

	private EmployeeType employeeType;

	private String phone;

	private String timeZone;

	private Gender gender;

	private LocalDate joinDate;

	private JobTitleDto jobTitle;

	private EEO eeo;

	private List<TeamEmployeeResponseDto> teams;

	private EmployeePersonalInfoDto personalInfo;

	private List<EmployeeFamilyDto> employeeFamilies;

	private List<EmployeeProgressionResponseDto> employeeProgressions;

	private EmployeeJobFamilyDto jobFamily;

	private List<ManagerDetailResponseDto> employees;

	private List<ManagingEmployeesResponseDto> managers;

	private List<EmployeeEmergencyDto> employeeEmergencies;

	private List<EmploymentVisaDto> employeeVisas;

	private List<EmployeeEducationDto> employeeEducations;

	private EmployeePeriodResponseDto periodResponseDto;

	private EmployeeRoleResponseDto userRoles;

}
