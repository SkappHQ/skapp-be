package com.skapp.community.peopleplanner.payload.request;

import com.skapp.community.common.type.Role;
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
