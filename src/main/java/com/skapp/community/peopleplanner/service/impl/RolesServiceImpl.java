package com.skapp.community.peopleplanner.service.impl;

import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.service.UserService;
import com.skapp.community.common.type.ModuleType;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.type.RoleLevel;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant;
import com.skapp.community.peopleplanner.constant.PeopleMessageConstant;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.model.EmployeeTimeline;
import com.skapp.community.peopleplanner.model.ModuleRoleRestriction;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.skapp.community.peopleplanner.payload.request.RoleRequestDto;
import com.skapp.community.peopleplanner.payload.response.AllowedModuleRolesResponseDto;
import com.skapp.community.peopleplanner.payload.response.AllowedRoleDto;
import com.skapp.community.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;
import com.skapp.community.peopleplanner.payload.response.RoleResponseDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeRoleDao;
import com.skapp.community.peopleplanner.repository.EmployeeTimelineDao;
import com.skapp.community.peopleplanner.repository.ModuleRoleRestrictionDao;
import com.skapp.community.peopleplanner.repository.TeamDao;
import com.skapp.community.peopleplanner.service.RolesService;
import com.skapp.community.peopleplanner.type.EmployeeTimelineType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolesServiceImpl implements RolesService {

	@NonNull
	private final EmployeeRoleDao employeeRoleDao;

	@NonNull
	private final UserService userService;

	@NonNull
	private final EmployeeDao employeeDao;

	@NonNull
	private final TeamDao teamDao;

	@NonNull
	private final EmployeeTimelineDao employeeTimelineDao;

	@NonNull
	private final PeopleMapper peopleMapper;

	@NonNull
	private final ModuleRoleRestrictionDao moduleRoleRestrictionDao;

	@NonNull
	private final MessageUtil messageUtil;

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

		List<EmployeeTimeline> employeeTimelines = new ArrayList<>();

		Optional<Employee> optionalEmployee = employeeDao.findById(employee.getEmployeeId());
		if (optionalEmployee.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND);
		}

		EmployeeRole employeeRole = createEmployeeRole(roleRequestDto, employee);
		employeeRoleDao.save(employeeRole);
		addSystemPermissionGrantedTimeline(employee, employeeTimelines, roleRequestDto,
				DateTimeUtils.getCurrentUtcDate());
		employeeTimelineDao.saveAll(employeeTimelines);

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

		if (employee.getEmployeeRole().getIsSuperAdmin() && employeeRoleDao.countByIsSuperAdminTrue() == 1) {
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

		List<EmployeeTimeline> employeeTimelines = new ArrayList<>();
		LocalDate currentDate = DateTimeUtils.getCurrentUtcDate();

		boolean isFirstTimeAssignment = isFirstTimeRoleAssignment(employeeRole);

		if (isFirstTimeAssignment) {
			addSystemPermissionGrantedTimeline(employee, employeeTimelines, roleRequestDto, currentDate);
		}
		else {
			checkAndAddRoleChangeTimeline(employee, employeeTimelines, oldEmployeeRole, roleRequestDto, currentDate);
		}

		User currentUser = userService.getCurrentUser();
		updateEmployeeRolesSafely(employeeRole, roleRequestDto, currentDate, currentUser);

		employeeRoleDao.save(employeeRole);

		if (!employeeTimelines.isEmpty()) {
			employeeTimelineDao.saveAll(employeeTimelines);
		}

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

	private void updateEmployeeRolesSafely(EmployeeRole employeeRole, RoleRequestDto roleRequestDto,
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
	}

	private boolean isFirstTimeRoleAssignment(EmployeeRole employeeRole) {
		return employeeRole.getPeopleRole() == null && employeeRole.getLeaveRole() == null
				&& employeeRole.getAttendanceRole() == null;
	}

	private void addSystemPermissionGrantedTimeline(Employee employee, List<EmployeeTimeline> employeeTimelines,
			RoleRequestDto roleRequestDto, LocalDate grantedDate) {

		if (roleRequestDto.getPeopleRole() != null) {
			employeeTimelines
				.add(getEmployeeTimeline(employee, null, roleRequestDto.getPeopleRole().name(), grantedDate));
		}

		if (roleRequestDto.getLeaveRole() != null) {
			employeeTimelines
				.add(getEmployeeTimeline(employee, null, roleRequestDto.getLeaveRole().name(), grantedDate));
		}

		if (roleRequestDto.getAttendanceRole() != null) {
			employeeTimelines
				.add(getEmployeeTimeline(employee, null, roleRequestDto.getAttendanceRole().name(), grantedDate));
		}
	}

	private void checkAndAddRoleChangeTimeline(Employee employee, List<EmployeeTimeline> employeeTimelines,
			EmployeeRole oldRole, RoleRequestDto newRole, LocalDate changedDate) {

		if (hasRoleChanged(oldRole.getPeopleRole(), newRole.getPeopleRole())) {
			employeeTimelines.add(getEmployeeTimeline(employee,
					oldRole.getPeopleRole() != null ? oldRole.getPeopleRole().name() : null,
					newRole.getPeopleRole().name(), changedDate));
		}

		if (hasRoleChanged(oldRole.getLeaveRole(), newRole.getLeaveRole())) {
			employeeTimelines.add(
					getEmployeeTimeline(employee, oldRole.getLeaveRole() != null ? oldRole.getLeaveRole().name() : null,
							newRole.getLeaveRole().name(), changedDate));
		}

		if (hasRoleChanged(oldRole.getAttendanceRole(), newRole.getAttendanceRole())) {
			employeeTimelines.add(getEmployeeTimeline(employee,
					oldRole.getAttendanceRole() != null ? oldRole.getAttendanceRole().name() : null,
					newRole.getAttendanceRole().name(), changedDate));
		}
	}

	private boolean hasRoleChanged(Role oldRole, Role newRole) {
		return (oldRole == null && newRole != null) || (oldRole != null && !oldRole.equals(newRole));
	}

	private EmployeeTimeline getEmployeeTimeline(Employee employee, String previousValue, String newValue,
			LocalDate displayDate) {
		EmployeeTimeline timeline = new EmployeeTimeline();
		timeline.setEmployee(employee);

		if (previousValue == null) {
			timeline.setTimelineType(EmployeeTimelineType.SYSTEM_PERMISSION_GRANTED);
			timeline.setTitle(EmployeeTimelineConstant.SYSTEM_PERMISSION_GRANTED);
		}
		else {
			timeline.setTimelineType(EmployeeTimelineType.SYSTEM_PERMISSION_CHANGED);
			timeline.setTitle(EmployeeTimelineConstant.SYSTEM_PERMISSION_CHANGED);
		}

		timeline.setPreviousValue(previousValue);
		timeline.setNewValue(newValue);
		timeline.setDisplayDate(displayDate);

		User currentUser = userService.getCurrentUser();
		if (currentUser != null) {
			timeline.setCreatedBy(currentUser.getUsername());
			timeline.setLastModifiedBy(currentUser.getUsername());
		}

		LocalDate now = DateTimeUtils.getCurrentUtcDate();
		timeline.setCreatedDate(now.atStartOfDay());
		timeline.setLastModifiedDate(now.atStartOfDay());

		return timeline;
	}

	@Override
	public ResponseEntityDto getAllowedRoles() {
		log.info("getAllowedRoles: execution started");

		List<AllowedModuleRolesResponseDto> allowedModuleRolesResponseDtos = new ArrayList<>();
		for (ModuleType module : ModuleType.values()) {
			if (module == ModuleType.COMMON) {
				continue;
			}

			Optional<ModuleRoleRestriction> optionalModuleRoleRestriction = moduleRoleRestrictionDao.findById(module);

			boolean isAdminAllowed = true;
			boolean isManagerAllowed = true;

			if (optionalModuleRoleRestriction.isPresent()) {
				ModuleRoleRestriction moduleRoleRestriction = optionalModuleRoleRestriction.get();
				isAdminAllowed = !Boolean.TRUE.equals(moduleRoleRestriction.getIsAdmin());
				isManagerAllowed = !Boolean.TRUE.equals(moduleRoleRestriction.getIsManager());
			}

			List<AllowedRoleDto> rolesForModule = new ArrayList<>();
			addAllowedRolesForModule(rolesForModule, module, isAdminAllowed, isManagerAllowed);

			AllowedModuleRolesResponseDto moduleResponse = new AllowedModuleRolesResponseDto();
			moduleResponse.setModule(module);
			moduleResponse.setRoles(rolesForModule);

			allowedModuleRolesResponseDtos.add(moduleResponse);
		}

		log.info("getAllowedRoles: execution ended");
		return new ResponseEntityDto(false, allowedModuleRolesResponseDtos);
	}

	@Override
	public ResponseEntityDto getSuperAdminCount() {
		log.info("getSuperAdminCount: execution started");

		long superAdminCount = employeeRoleDao.countByIsSuperAdminTrue();

		log.info("getSuperAdminCount: execution ended");
		return new ResponseEntityDto(false, superAdminCount);
	}

	private void addAllowedRolesForModule(List<AllowedRoleDto> rolesList, ModuleType module, boolean isAdminAllowed,
			boolean isManagerAllowed) {
		if (isAdminAllowed) {
			rolesList.add(createAllowedRole(RoleLevel.ADMIN.getDisplayName(),
					getRoleForModuleAndLevel(module, RoleLevel.ADMIN)));
		}

		if (isManagerAllowed) {
			rolesList.add(createAllowedRole(RoleLevel.MANAGER.getDisplayName(),
					getRoleForModuleAndLevel(module, RoleLevel.MANAGER)));
		}

		rolesList.add(createAllowedRole(RoleLevel.EMPLOYEE.getDisplayName(),
				getRoleForModuleAndLevel(module, RoleLevel.EMPLOYEE)));
	}

	private AllowedRoleDto createAllowedRole(String roleName, Role role) {
		AllowedRoleDto allowedRole = new AllowedRoleDto();
		allowedRole.setName(roleName);
		allowedRole.setRole(role);
		return allowedRole;
	}

	private Role getRoleForModuleAndLevel(ModuleType module, RoleLevel roleLevel) {
		return switch (module) {
			case ATTENDANCE -> switch (roleLevel) {
				case ADMIN -> Role.ATTENDANCE_ADMIN;
				case MANAGER -> Role.ATTENDANCE_MANAGER;
				case EMPLOYEE -> Role.ATTENDANCE_EMPLOYEE;
			};
			case PEOPLE -> switch (roleLevel) {
				case ADMIN -> Role.PEOPLE_ADMIN;
				case MANAGER -> Role.PEOPLE_MANAGER;
				case EMPLOYEE -> Role.PEOPLE_EMPLOYEE;
			};
			case LEAVE -> switch (roleLevel) {
				case ADMIN -> Role.LEAVE_ADMIN;
				case MANAGER -> Role.LEAVE_MANAGER;
				case EMPLOYEE -> Role.LEAVE_EMPLOYEE;
			};
			default -> null;
		};
	}

	private EmployeeRole createEmployeeRole(RoleRequestDto roleRequestDto, Employee employee) {
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

}
