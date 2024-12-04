package com.rootcode.skapp.peopleplanner.payload.request;

import com.rootcode.skapp.common.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDto {

	private Boolean isSuperAdmin;

	private Role attendanceRole;

	private Role peopleRole;

	private Role leaveRole;

}
