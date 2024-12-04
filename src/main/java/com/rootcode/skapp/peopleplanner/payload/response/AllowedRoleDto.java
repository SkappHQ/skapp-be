package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllowedRoleDto {

	private String name;

	private Role role;

}
