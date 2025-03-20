package com.skapp.community.peopleplanner.service.impl;

import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.exception.ValidationException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.service.UserService;
import com.skapp.community.common.service.UserVersionService;
import com.skapp.community.common.type.ModuleType;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.type.RoleLevel;
import com.skapp.community.common.type.VersionType;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.peopleplanner.constant.PeopleMessageConstant;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.model.ModuleRoleRestriction;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.skapp.community.peopleplanner.payload.request.RoleRequestDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeSystemPermissionsDto;
import com.skapp.community.peopleplanner.payload.response.AllowedModuleRolesResponseDto;
import com.skapp.community.peopleplanner.payload.response.AllowedRoleDto;
import com.skapp.community.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;
import com.skapp.community.peopleplanner.payload.response.RoleResponseDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeRoleDao;
import com.skapp.community.peopleplanner.repository.ModuleRoleRestrictionDao;
import com.skapp.community.peopleplanner.repository.TeamDao;
import com.skapp.community.peopleplanner.service.RolesService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RolesServiceImpl implements RolesService {

	private final EmployeeRoleDao employeeRoleDao;

	@Getter
	private final UserService userService;

	private final EmployeeDao employeeDao;

	private final TeamDao teamDao;

	private final PeopleMapper peopleMapper;

	private final ModuleRoleRestrictionDao moduleRoleRestrictionDao;

	private final MessageUtil messageUtil;

	private final UserVersionService userVersionService;

	@Override
	public ResponseEntityDto getSystemRoles() {
		log.info("getSystemRoles: execution started");

		List<RoleResponseDto> roleResponseDtos = new ArrayList<>();

		for (ModuleType moduleType : ModuleType.values()) {
			if (moduleType != ModuleType.COMMON) {
				roleResponseDtos.add(createRoleResponseDto(moduleType));
			}
		}

		log.info("getSystemRoles: execution ended");
		return new ResponseEntityDto(false, roleResponseDtos);
	}

	@Override
	public void assignRolesToEmployee(RoleRequestDto roleRequestDto, Employee employee) {
		log.info("assignRolesToEmployee: execution started");

		Optional<Employee> optionalEmployee = employeeDao.findById(employee.getEmployeeId());
		if (optionalEmployee.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND);
		}

		EmployeeRole employeeRole = createEmployeeRole(roleRequestDto, employee);
		employeeRoleDao.save(employeeRole);

		log.info("assignRolesToEmployee: execution ended");
		new ResponseEntityDto(false, peopleMapper.employeeRoleToEmployeeRoleResponseDto(employeeRole));
	}

	@Override
	public ResponseEntityDto updateRoleRestrictions(ModuleRoleRestrictionRequestDto moduleRoleRestrictionRequestDto) {
		log.info("updateRoleRestrictions: execution started");

		ModuleRoleRestriction moduleRoleRestriction = peopleMapper
			.roleRestrictionRequestDtoToRestrictRole(moduleRoleRestrictionRequestDto);
		moduleRoleRestrictionDao.save(moduleRoleRestriction);

		log.info("updateRoleRestrictions: execution ended");
		return new ResponseEntityDto(false, messageUtil.getMessage(PeopleMessageConstant.PEOPLE_SUCCESS_ROLE_RESTRICT));
	}

	@Override
	public ModuleRoleRestrictionResponseDto getRestrictedRoleByModule(ModuleType module) {
		log.info("getRestrictedRoles: execution started");

		Optional<ModuleRoleRestriction> restrictedRole = moduleRoleRestrictionDao.findById(module);
		if (restrictedRole.isEmpty()) {
			ModuleRoleRestrictionResponseDto newRestrictRole = new ModuleRoleRestrictionResponseDto();
			newRestrictRole.setModule(module);
			newRestrictRole.setIsAdmin(false);
			newRestrictRole.setIsManager(false);

			return newRestrictRole;
		}

		ModuleRoleRestriction moduleRoleRestriction = restrictedRole.get();
		ModuleRoleRestrictionResponseDto moduleRoleRestrictionResponseDto = peopleMapper
			.restrictRoleToRestrictRoleResponseDto(moduleRoleRestriction);

		log.info("getRestrictedRoles: execution ended");
		return moduleRoleRestrictionResponseDto;
	}

	@Override
	public void updateEmployeeRoles(RoleRequestDto roleRequestDto, Employee employee) {
		log.info("updateEmployeeRoles: execution started");

		if (Boolean.TRUE.equals(employee.getEmployeeRole().getIsSuperAdmin())
				&& employeeRoleDao.countByIsSuperAdminTrue() == 1 && isUserRoleDowngraded(roleRequestDto)) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_ONLY_ONE_SUPER_ADMIN);
		}

		Optional<EmployeeRole> optionalEmployeeRole = employeeRoleDao.findById(employee.getEmployeeId());
		if (optionalEmployeeRole.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND);
		}

		if (isEmployeeDemoted(roleRequestDto, employee)) {
			List<Team> teams = teamDao.findTeamsManagedByUser(employee.getEmployeeId(), true);

			Long managingEmployeeCount = employeeDao.countEmployeesByManagerId(employee.getEmployeeId());

			if (!teams.isEmpty()) {
				throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_LEADING_TEAMS);
			}

			if (managingEmployeeCount > 0) {
				throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_SUPERVISING_EMPLOYEES);
			}
		}

		EmployeeRole employeeRole = optionalEmployeeRole.get();
		EmployeeRole oldEmployeeRole = new EmployeeRole();

		oldEmployeeRole.setPeopleRole(employeeRole.getPeopleRole());
		oldEmployeeRole.setLeaveRole(employeeRole.getLeaveRole());
		oldEmployeeRole.setAttendanceRole(employeeRole.getAttendanceRole());
		oldEmployeeRole.setIsSuperAdmin(employeeRole.getIsSuperAdmin());

		LocalDate currentDate = DateTimeUtils.getCurrentUtcDate();

		User currentUser = userService.getCurrentUser();
		employeeRole = updateEmployeeRolesSafely(employeeRole, roleRequestDto, currentDate, currentUser);

		employeeRoleDao.save(employeeRole);

		userVersionService.upgradeUserVersion(employee.getUser().getUserId(), VersionType.MINOR);

		log.info("updateEmployeeRoles: execution ended");
	}

	private boolean isEmployeeDemoted(RoleRequestDto roleRequestDto, Employee employee) {
		Boolean isPeopleDemoted = (employee.getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_MANAGER)
				|| employee.getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_ADMIN))
				&& roleRequestDto.getPeopleRole().equals(Role.PEOPLE_EMPLOYEE);
		Boolean isAttendanceDemoted = (employee.getEmployeeRole().getAttendanceRole().equals(Role.ATTENDANCE_MANAGER)
				|| employee.getEmployeeRole().getAttendanceRole().equals(Role.ATTENDANCE_ADMIN))
				&& roleRequestDto.getAttendanceRole().equals(Role.ATTENDANCE_EMPLOYEE);
		Boolean isLeaveDemoted = (employee.getEmployeeRole().getLeaveRole().equals(Role.LEAVE_MANAGER)
				|| employee.getEmployeeRole().getLeaveRole().equals(Role.LEAVE_ADMIN))
				&& roleRequestDto.getLeaveRole().equals(Role.LEAVE_EMPLOYEE);

		return isPeopleDemoted || isAttendanceDemoted || isLeaveDemoted;
	}

	protected EmployeeRole updateEmployeeRolesSafely(EmployeeRole employeeRole, RoleRequestDto roleRequestDto,
			LocalDate currentDate, User currentUser) {
		if (employeeRole != null) {
			employeeRole.setPeopleRole(roleRequestDto.getPeopleRole());
			employeeRole.setLeaveRole(roleRequestDto.getLeaveRole());
			employeeRole.setAttendanceRole(roleRequestDto.getAttendanceRole());
			employeeRole.setIsSuperAdmin(roleRequestDto.getIsSuperAdmin());
			employeeRole.setChangedDate(currentDate);

			if (currentUser != null && currentUser.getEmployee() != null) {
				employeeRole.setRoleChangedBy(currentUser.getEmployee());
			}
		}
		return employeeRole;
	}

	@Override
	public ResponseEntityDto getAllowedRoles() {
		log.info("getAllowedRoles: execution started");

		Map<ModuleType, List<RoleLevel>> moduleTypeListMap = initializeRolesForModule();
		List<AllowedModuleRolesResponseDto> allowedModuleRolesResponseDtos = moduleTypeListMap.entrySet()
			.stream()
			.map(this::processModuleRoles)
			.toList();

		log.info("getAllowedRoles: execution ended");
		return new ResponseEntityDto(false, allowedModuleRolesResponseDtos);
	}

	private AllowedModuleRolesResponseDto processModuleRoles(Map.Entry<ModuleType, List<RoleLevel>> entry) {
		Optional<EmployeeRole> employeeRoleOpt = employeeRoleDao.findById(userService.getCurrentUser().getUserId());
		boolean isSuperAdmin = employeeRoleOpt.map(EmployeeRole::getIsSuperAdmin).orElse(false);

		ModuleType module = entry.getKey();
		List<RoleLevel> prebuiltRoles = entry.getValue();

		ModuleRoleRestriction moduleRoleRestriction = moduleRoleRestrictionDao.findById(module).orElse(null);

		boolean isAdminAllowed = isSuperAdmin
				|| (moduleRoleRestriction == null || Boolean.FALSE.equals(moduleRoleRestriction.getIsAdmin()));
		boolean isManagerAllowed = isSuperAdmin
				|| (moduleRoleRestriction == null || Boolean.FALSE.equals(moduleRoleRestriction.getIsManager()));

		List<AllowedRoleDto> rolesForModule = prebuiltRoles.stream()
			.filter(roleLevel -> isRoleAllowed(roleLevel, isAdminAllowed, isManagerAllowed))
			.map(roleLevel -> createAllowedRole(roleLevel.getDisplayName(),
					getRoleForModuleAndLevel(module, roleLevel)))
			.toList();

		AllowedModuleRolesResponseDto moduleResponse = new AllowedModuleRolesResponseDto();
		moduleResponse.setModule(module);
		moduleResponse.setRoles(rolesForModule);
		return moduleResponse;
	}

	// Helper method to determine if a role is allowed based on restrictions
	private boolean isRoleAllowed(RoleLevel roleLevel, boolean isAdminAllowed, boolean isManagerAllowed) {
		return switch (roleLevel) {
			case ADMIN -> isAdminAllowed;
			case MANAGER -> isManagerAllowed;
			default -> true; // other roles are always allowed
		};
	}

	protected Map<ModuleType, List<RoleLevel>> initializeRolesForModule() {
		Map<ModuleType, List<RoleLevel>> roles = new EnumMap<>(ModuleType.class);

		roles.put(ModuleType.ATTENDANCE, List.of(RoleLevel.ADMIN, RoleLevel.MANAGER, RoleLevel.EMPLOYEE));
		roles.put(ModuleType.PEOPLE, List.of(RoleLevel.ADMIN, RoleLevel.MANAGER, RoleLevel.EMPLOYEE));
		roles.put(ModuleType.LEAVE, List.of(RoleLevel.ADMIN, RoleLevel.MANAGER, RoleLevel.EMPLOYEE));

		return roles;
	}

	@Override
	public ResponseEntityDto getSuperAdminCount() {
		log.info("getSuperAdminCount: execution started");

		long superAdminCount = employeeRoleDao.countByIsSuperAdminTrue();

		log.info("getSuperAdminCount: execution ended");
		return new ResponseEntityDto(false, superAdminCount);
	}

	@Override
	public void saveEmployeeRoles(@NotNull Employee employee) {
		log.info("saveEmployeeRoles: execution started");

		EmployeeRole employeeRole = setupBulkEmployeeRoles(employee);

		employeeRoleDao.save(employeeRole);
		employee.setEmployeeRole(employeeRole);

		log.info("saveEmployeeRoles: execution started");
	}

	@Override
	public EmployeeRole setupBulkEmployeeRoles(Employee employee) {
		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(employee);
		employeeRole.setPeopleRole(Role.PEOPLE_EMPLOYEE);
		employeeRole.setLeaveRole(Role.LEAVE_EMPLOYEE);
		employeeRole.setAttendanceRole(Role.ATTENDANCE_EMPLOYEE);
		employeeRole.setIsSuperAdmin(false);
		employeeRole.setChangedDate(DateTimeUtils.getCurrentUtcDate());
		employeeRole.setRoleChangedBy(employee);
		return employeeRole;
	}

	public void validateRoles(EmployeeSystemPermissionsDto userRoles) {
		if (userRoles == null) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_SYSTEM_PERMISSION_REQUIRED);
		}

		if (userRoles.getIsSuperAdmin() == null) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_SUPER_ADMIN_REQUIRED);
		}

		if (userRoles.getPeopleRole() == null) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_PEOPLE_ROLE_REQUIRED);
		}

		Role peopleRole = userRoles.getPeopleRole();
		EnumSet<Role> validPeopleRoles = EnumSet.of(Role.PEOPLE_EMPLOYEE, Role.PEOPLE_MANAGER, Role.PEOPLE_ADMIN);
		if (!validPeopleRoles.contains(peopleRole)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_PEOPLE_ROLE,
					new String[] { peopleRole.name() });
		}

		if (userRoles.getAttendanceRole() == null) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_ATTENDANCE_ROLE_REQUIRED);
		}

		Role attendanceRole = userRoles.getAttendanceRole();
		EnumSet<Role> validAttendanceRoles = EnumSet.of(Role.ATTENDANCE_EMPLOYEE, Role.ATTENDANCE_MANAGER,
				Role.ATTENDANCE_ADMIN);
		if (!validAttendanceRoles.contains(attendanceRole)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_ATTENDANCE_ROLE,
					new String[] { attendanceRole.name() });
		}

		if (userRoles.getLeaveRole() == null) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_LEAVE_ROLE_REQUIRED);
		}

		Role leaveRole = userRoles.getLeaveRole();
		EnumSet<Role> validLeaveRoles = EnumSet.of(Role.LEAVE_EMPLOYEE, Role.LEAVE_MANAGER, Role.LEAVE_ADMIN);
		if (!validLeaveRoles.contains(leaveRole)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_LEAVE_ROLE,
					new String[] { leaveRole.name() });
		}

		if (Boolean.TRUE.equals(userRoles.getIsSuperAdmin())
				&& (userRoles.getPeopleRole() != Role.PEOPLE_ADMIN || userRoles.getLeaveRole() != Role.LEAVE_ADMIN
						|| userRoles.getAttendanceRole() != Role.ATTENDANCE_ADMIN)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_SHOULD_ASSIGN_PROPER_PERMISSIONS);
		}

		User currentUser = userService.getCurrentUser();
		if (hasOnlyPeopleAdminPermissions(currentUser)
				&& validateRestrictedRoleAssignment(userRoles.getAttendanceRole(), ModuleType.ATTENDANCE)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_ATTENDANCE_RESTRICTED_ROLE_ACCESS,
					new String[] { userRoles.getAttendanceRole().name() });
		}

		if (hasOnlyPeopleAdminPermissions(currentUser)
				&& validateRestrictedRoleAssignment(userRoles.getPeopleRole(), ModuleType.PEOPLE)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_PEOPLE_RESTRICTED_ROLE_ACCESS,
					new String[] { userRoles.getPeopleRole().name() });
		}

		if (hasOnlyPeopleAdminPermissions(currentUser)
				&& validateRestrictedRoleAssignment(userRoles.getLeaveRole(), ModuleType.LEAVE)) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_LEAVE_RESTRICTED_ROLE_ACCESS,
					new String[] { userRoles.getLeaveRole().name() });
		}
	}

	@Override
	public void saveSuperAdminRoles(Employee employee) {
		log.info("saveSuperAdminRoles: execution started");

		EmployeeRole superAdminRoles = new EmployeeRole();
		superAdminRoles.setEmployee(employee);
		superAdminRoles.setPeopleRole(Role.PEOPLE_ADMIN);
		superAdminRoles.setLeaveRole(Role.LEAVE_ADMIN);
		superAdminRoles.setAttendanceRole(Role.ATTENDANCE_ADMIN);
		superAdminRoles.setIsSuperAdmin(true);
		superAdminRoles.setChangedDate(DateTimeUtils.getCurrentUtcDate());
		superAdminRoles.setRoleChangedBy(employee);

		employeeRoleDao.save(superAdminRoles);
		employee.setEmployeeRole(superAdminRoles);

		log.info("saveSuperAdminRoles: execution ended");
	}

	protected boolean hasOnlyPeopleAdminPermissions(User currentUser) {
		return Boolean.FALSE.equals(currentUser.getEmployee().getEmployeeRole().getIsSuperAdmin())
				&& currentUser.getEmployee().getEmployeeRole().getPeopleRole() == Role.PEOPLE_ADMIN;
	}

	protected Boolean validateRestrictedRoleAssignment(Role role, ModuleType moduleType) {
		ModuleRoleRestrictionResponseDto restrictedRole = getRestrictedRoleByModule(moduleType);

		if (role == Role.PEOPLE_ADMIN || role == Role.ATTENDANCE_ADMIN || role == Role.LEAVE_ADMIN) {
			return Boolean.TRUE.equals(restrictedRole.getIsAdmin());
		}

		if (role == Role.PEOPLE_MANAGER || role == Role.ATTENDANCE_MANAGER || role == Role.LEAVE_MANAGER) {
			return Boolean.TRUE.equals(restrictedRole.getIsManager());
		}

		return false;
	}

	private AllowedRoleDto createAllowedRole(String roleName, Role role) {
		AllowedRoleDto allowedRole = new AllowedRoleDto();
		allowedRole.setName(roleName);
		allowedRole.setRole(role);
		return allowedRole;
	}

	protected Role getRoleForModuleAndLevel(ModuleType module, RoleLevel roleLevel) {
		return switch (module) {
			case ATTENDANCE -> switch (roleLevel) {
				case ADMIN -> Role.ATTENDANCE_ADMIN;
				case MANAGER -> Role.ATTENDANCE_MANAGER;
				case EMPLOYEE -> Role.ATTENDANCE_EMPLOYEE;
				default -> null;
			};
			case PEOPLE -> switch (roleLevel) {
				case ADMIN -> Role.PEOPLE_ADMIN;
				case MANAGER -> Role.PEOPLE_MANAGER;
				case EMPLOYEE -> Role.PEOPLE_EMPLOYEE;
				default -> null;
			};
			case LEAVE -> switch (roleLevel) {
				case ADMIN -> Role.LEAVE_ADMIN;
				case MANAGER -> Role.LEAVE_MANAGER;
				case EMPLOYEE -> Role.LEAVE_EMPLOYEE;
				default -> null;
			};
			default -> null;
		};
	}

	protected EmployeeRole createEmployeeRole(RoleRequestDto roleRequestDto, Employee employee) {
		EmployeeRole employeeRole = new EmployeeRole();
		User currentUser = userService.getCurrentUser();

		employeeRole.setEmployee(employee);
		employeeRole.setPeopleRole(roleRequestDto.getPeopleRole());
		employeeRole.setLeaveRole(roleRequestDto.getLeaveRole());
		employeeRole.setAttendanceRole(roleRequestDto.getAttendanceRole());
		employeeRole.setIsSuperAdmin(roleRequestDto.getIsSuperAdmin());
		employeeRole.setChangedDate(DateTimeUtils.getCurrentUtcDate());
		employeeRole.setRoleChangedBy(currentUser.getEmployee());

		return employeeRole;
	}

	private RoleResponseDto createRoleResponseDto(ModuleType moduleType) {
		RoleResponseDto roleResponseDto = new RoleResponseDto();
		String capitalizedModuleName = moduleType.getDisplayName().substring(0, 1).toUpperCase()
				+ moduleType.getDisplayName().substring(1).toLowerCase();
		roleResponseDto.setModule(capitalizedModuleName);

		List<String> roles = new ArrayList<>();
		roles.add(RoleLevel.ADMIN.getDisplayName());
		roles.add(RoleLevel.MANAGER.getDisplayName());
		roles.add(RoleLevel.EMPLOYEE.getDisplayName());

		roleResponseDto.setRoles(roles);
		return roleResponseDto;
	}

	private boolean isUserRoleDowngraded(RoleRequestDto roleRequestDto) {
		return roleRequestDto.getPeopleRole() == null || !roleRequestDto.getPeopleRole().equals(Role.PEOPLE_ADMIN)
				|| !roleRequestDto.getAttendanceRole().equals(Role.ATTENDANCE_ADMIN)
				|| !roleRequestDto.getLeaveRole().equals(Role.LEAVE_ADMIN);
	}

}
