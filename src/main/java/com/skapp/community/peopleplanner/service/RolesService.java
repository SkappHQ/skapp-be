package com.skapp.community.peopleplanner.service;

import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.type.ModuleType;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.skapp.community.peopleplanner.payload.request.RoleRequestDto;
import com.skapp.community.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;

public interface RolesService {

	ResponseEntityDto getSystemRoles();

	void assignRolesToEmployee(RoleRequestDto roleRequestDto, Employee employee);

	ResponseEntityDto updateRoleRestrictions(ModuleRoleRestrictionRequestDto moduleRoleRestrictionRequestDto);

	ModuleRoleRestrictionResponseDto getRestrictedRoleByModule(ModuleType moduleType);

	void updateEmployeeRoles(RoleRequestDto roleRequestDto, Employee employee);

	ResponseEntityDto getAllowedRoles();

	ResponseEntityDto getSuperAdminCount();

}
