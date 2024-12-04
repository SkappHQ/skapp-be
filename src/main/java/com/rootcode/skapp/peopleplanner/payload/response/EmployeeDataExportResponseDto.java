package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.JobFamilyDto;
import com.rootcode.skapp.peopleplanner.type.EmployeeType;
import com.rootcode.skapp.peopleplanner.type.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EmployeeDataExportResponseDto {

	private Long employeeId;

	private String email;

	private String firstName;

	private String lastName;

	private String designation;

	private String authPic;

	private String country;

	private String personalEmail;

	private String phone;

	private String identificationNo;

	private String timeZone;

	private String address;

	private Integer workHourCapacity;

	private EmployeeType employeeType;

	private Gender gender;

	private LocalDate joinDate;

	private List<TeamResponseDto> teamResponseDto;

	private JobFamilyDto jobFamily;

	private JobTitleDto jobTitle;

	private Boolean isActive;

	private List<EmployeeResponseDto> managers;

	private EmployeePeriodResponseDto employeePeriod;

}
