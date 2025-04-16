package com.skapp.community.peopleplanner.service;

import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.type.ModuleType;
import com.skapp.community.common.type.Role;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeSystemPermissionsDto;
import com.skapp.community.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;

import java.util.List;

public interface RolesService {

	ResponseEntityDto getSystemRoles();

	EmployeeRole assignRolesToEmployee(EmployeeSystemPermissionsDto roleRequestDto, Employee employee);

	ResponseEntityDto updateRoleRestrictions(ModuleRoleRestrictionRequestDto moduleRoleRestrictionRequestDto);

	ModuleRoleRestrictionResponseDto getRestrictedRoleByModule(ModuleType moduleType);

	ResponseEntityDto getAllowedRoles();

	ResponseEntityDto getSuperAdminCount();

	void saveEmployeeRoles(Employee employee);

	void validateRoles(EmployeeSystemPermissionsDto userRoles, User user);

	void saveSuperAdminRoles(Employee employee);

	EmployeeRole setupBulkEmployeeRoles(Employee employee);

	List<EmployeeManager> filterManagersByRoles(List<EmployeeManager> managers, List<Role> roles);

}
