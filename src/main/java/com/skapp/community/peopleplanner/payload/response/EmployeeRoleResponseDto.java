package com.skapp.community.peopleplanner.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skapp.community.common.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRoleResponseDto {

	private Role peopleRole;

	private Role leaveRole;

	private Role attendanceRole;

	@JsonProperty("eSignRole")
	private Role eSignRole;

	private Boolean isSuperAdmin;

}
