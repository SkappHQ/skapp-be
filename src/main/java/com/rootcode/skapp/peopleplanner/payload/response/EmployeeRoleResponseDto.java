package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRoleResponseDto {

	private Role peopleRole;

	private Role leaveRole;

	private Role attendanceRole;

	private Boolean isSuperAdmin;

}
