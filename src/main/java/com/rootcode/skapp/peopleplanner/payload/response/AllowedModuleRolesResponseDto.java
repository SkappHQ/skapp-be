package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.type.ModuleType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllowedModuleRolesResponseDto {

	private ModuleType module;

	private List<AllowedRoleDto> roles;

}
