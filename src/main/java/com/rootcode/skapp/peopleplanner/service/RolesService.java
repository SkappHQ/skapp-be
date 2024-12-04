package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.common.type.ModuleType;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.RoleRequestDto;
import com.rootcode.skapp.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;

public interface RolesService {

	ResponseEntityDto getSystemRoles();

	void assignRolesToEmployee(RoleRequestDto roleRequestDto, Employee employee);

	ResponseEntityDto updateRoleRestrictions(ModuleRoleRestrictionRequestDto moduleRoleRestrictionRequestDto);

	ModuleRoleRestrictionResponseDto getRestrictedRoleByModule(ModuleType moduleType);

	void updateEmployeeRoles(RoleRequestDto roleRequestDto, Employee employee);

	ResponseEntityDto getAllowedRoles();

	ResponseEntityDto getSuperAdminCount();

}
