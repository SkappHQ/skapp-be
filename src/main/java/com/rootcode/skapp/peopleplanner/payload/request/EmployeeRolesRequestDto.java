package com.rootcode.skapp.peopleplanner.payload.request;

import com.rootcode.skapp.common.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRolesRequestDto {

	public Role leaveRole;

	public Role peopleRole;

	public Role attendanceRole;

	public Boolean isSuperAdmin;

}
