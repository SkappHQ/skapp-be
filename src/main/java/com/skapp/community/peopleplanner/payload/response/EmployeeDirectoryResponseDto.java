package com.skapp.community.peopleplanner.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeDirectoryResponseDto {

	private Long employeeId;

	private String firstName;

	private String lastName;

	private String authPic;

	private String email;

	private String jobFamily;

	private String jobTitle;

	private Boolean isActive;

	private List<EmployeeManagerResponseDto> managers;

	private List<EmployeeDirectoryTeamResponseDto> teams;

}
