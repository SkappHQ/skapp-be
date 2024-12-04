package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.type.ModuleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleRoleRestrictionResponseDto {

	private ModuleType module;

	private Boolean isAdmin;

	private Boolean isManager;

}
