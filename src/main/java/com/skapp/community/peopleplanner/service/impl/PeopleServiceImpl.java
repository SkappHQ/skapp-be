package com.skapp.community.peopleplanner.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.EntityNotFoundException;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.exception.ValidationException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.model.UserSettings;
import com.skapp.community.common.payload.response.BulkStatusSummary;
import com.skapp.community.common.payload.response.NotificationSettingsResponseDto;
import com.skapp.community.common.payload.response.PageDto;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.repository.UserDao;
import com.skapp.community.common.service.BulkContextService;
import com.skapp.community.common.service.EncryptionDecryptionService;
import com.skapp.community.common.service.UserService;
import com.skapp.community.common.service.impl.AsyncEmailServiceImpl;
import com.skapp.community.common.type.LoginMethod;
import com.skapp.community.common.type.ModuleType;
import com.skapp.community.common.type.NotificationSettingsType;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.CommonModuleUtils;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.common.util.Validation;
import com.skapp.community.common.util.transformer.PageTransformer;
import com.skapp.community.leaveplanner.type.ManagerType;
import com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant;
import com.skapp.community.peopleplanner.constant.PeopleConstants;
import com.skapp.community.peopleplanner.constant.PeopleMessageConstant;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeEducation;
import com.skapp.community.peopleplanner.model.EmployeeEmergency;
import com.skapp.community.peopleplanner.model.EmployeeFamily;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import com.skapp.community.peopleplanner.model.EmployeePeriod;
import com.skapp.community.peopleplanner.model.EmployeePersonalInfo;
import com.skapp.community.peopleplanner.model.EmployeeProgression;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.model.EmployeeTeam;
import com.skapp.community.peopleplanner.model.EmployeeTimeline;
import com.skapp.community.peopleplanner.model.EmployeeVisa;
import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.model.JobTitle;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.peopleplanner.payload.request.EmployeeBulkDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDataValidationDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeEducationDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeEmergencyDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeFamilyDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeFilterDto;
import com.skapp.community.peopleplanner.payload.request.EmployeePersonalInfoDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeProgressionsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeQuickAddDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeRolesRequestDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.EmploymentVisaDto;
import com.skapp.community.peopleplanner.payload.request.JobTitleDto;
import com.skapp.community.peopleplanner.payload.request.NotificationSettingsPatchRequestDto;
import com.skapp.community.peopleplanner.payload.request.PermissionFilterDto;
import com.skapp.community.peopleplanner.payload.request.ProbationPeriodDto;
import com.skapp.community.peopleplanner.payload.request.RoleRequestDto;
import com.skapp.community.peopleplanner.payload.response.AnalyticsSearchResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeBulkErrorResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeBulkResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeCountDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeCredentialsResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeDataExportResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeDataValidationResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeDetailedResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeJobFamilyDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeManagerDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeManagerResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeePeriodResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeProgressionResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeTeamDto;
import com.skapp.community.peopleplanner.payload.response.ManagerEmployeeDto;
import com.skapp.community.peopleplanner.payload.response.ManagingEmployeesResponseDto;
import com.skapp.community.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;
import com.skapp.community.peopleplanner.payload.response.SummarizedEmployeeDtoForEmployees;
import com.skapp.community.peopleplanner.payload.response.SummarizedManagerEmployeeDto;
import com.skapp.community.peopleplanner.payload.response.TeamDetailResponseDto;
import com.skapp.community.peopleplanner.payload.response.TeamEmployeeResponseDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeEducationDao;
import com.skapp.community.peopleplanner.repository.EmployeeFamilyDao;
import com.skapp.community.peopleplanner.repository.EmployeeManagerDao;
import com.skapp.community.peopleplanner.repository.EmployeePeriodDao;
import com.skapp.community.peopleplanner.repository.EmployeeProgressionDao;
import com.skapp.community.peopleplanner.repository.EmployeeRoleDao;
import com.skapp.community.peopleplanner.repository.EmployeeTeamDao;
import com.skapp.community.peopleplanner.repository.EmployeeTimelineDao;
import com.skapp.community.peopleplanner.repository.EmployeeVisaDao;
import com.skapp.community.peopleplanner.repository.JobFamilyDao;
import com.skapp.community.peopleplanner.repository.JobTitleDao;
import com.skapp.community.peopleplanner.repository.TeamDao;
import com.skapp.community.peopleplanner.service.EmployeeTimelineService;
import com.skapp.community.peopleplanner.service.PeopleEmailService;
import com.skapp.community.peopleplanner.service.PeopleService;
import com.skapp.community.peopleplanner.service.RolesService;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.BulkItemStatus;
import com.skapp.community.peopleplanner.type.EmployeeTimelineType;
import com.skapp.community.peopleplanner.type.EmployeeType;
import com.skapp.community.peopleplanner.util.Validations;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.skapp.community.common.util.Validation.ADDRESS_REGEX;
import static com.skapp.community.common.util.Validation.ALPHANUMERIC_REGEX;
import static com.skapp.community.common.util.Validation.NAME_REGEX;
import static com.skapp.community.common.util.Validation.SPECIAL_CHAR_REGEX;
import static com.skapp.community.common.util.Validation.VALID_NIN_NUMBER_REGEXP;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_EMPLOYMENT_TYPE_CHANGED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_JOB_FAMILY_CHANGED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_JOB_TITLE_CHANGED;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService {

	@NonNull
	private final UserService userService;

	@NonNull
	private final MessageUtil messageUtil;

	@NonNull
	private final PeopleMapper peopleMapper;

	@NonNull
	private final UserDao userDao;

	@NonNull
	private final TeamDao teamDao;

	@NonNull
	private final EmployeeDao employeeDao;

	@NonNull
	private final JobFamilyDao jobFamilyDao;

	@NonNull
	private final EmployeeProgressionDao employeeProgressionDao;

	@NonNull
	private final JobTitleDao jobTitleDao;

	@NonNull
	private final EmployeeRoleDao employeeRoleDao;

	@NonNull
	private final EmployeePeriodDao employeePeriodDao;

	@NonNull
	private final EmployeeVisaDao employeeVisaDao;

	@NonNull
	private final EmployeeEducationDao employeeEducationDao;

	@NonNull
	private final EmployeeFamilyDao employeeFamilyDao;

	@NonNull
	private final EmployeeTeamDao employeeTeamDao;

	@NonNull
	private final EmployeeTimelineDao employeeTimelineDao;

	@NonNull
	private final EmployeeManagerDao employeeManagerDao;

	@NonNull
	private final EmployeeTimelineService employeeTimelineService;

	@NonNull
	private final PasswordEncoder passwordEncoder;

	@NonNull
	private final RolesService rolesService;

	@NonNull
	private final PageTransformer pageTransformer;

	@NonNull
	private final PlatformTransactionManager transactionManager;

	@NonNull
	private final PeopleEmailService peopleEmailService;

	@NonNull
	private final ObjectMapper mapper;

	@NonNull
	private final EncryptionDecryptionService encryptionDecryptionService;

	@NonNull
	private final BulkContextService bulkContextService;

	private final AsyncEmailServiceImpl asyncEmailServiceImpl;

	@Value("${encryptDecryptAlgorithm.secret}")
	private String encryptSecret;

	private static final String START_DATE = "startDate";

	private static final String END_DATE = "endDate";

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ResponseEntityDto addNewEmployee(@NonNull EmployeeDetailsDto employeeDetailsDto) {
		log.info("addNewEmployee: execution started");

		// Validate the roles
		validateRoles(employeeDetailsDto.getUserRoles());

		// Validate the employee details
		Validations.validateEmployeeDetails(employeeDetailsDto);

		Employee finalEmployee = peopleMapper.employeeDetailsDtoToEmployee(employeeDetailsDto);

		processEmploymentDetails(employeeDetailsDto, finalEmployee);
		processEmployeeProgressions(employeeDetailsDto, finalEmployee);
		processEmployeeEmergencyContacts(employeeDetailsDto, finalEmployee);
		processEmployeePersonalInfo(employeeDetailsDto, finalEmployee);
		processEmployeeVisas(employeeDetailsDto, finalEmployee);
		processEmployeeFamilies(employeeDetailsDto, finalEmployee);
		processEmployeeEducations(employeeDetailsDto, finalEmployee);

		finalEmployee.setAccountStatus(AccountStatus.PENDING);

		User user = new User();
		user.setEmail(employeeDetailsDto.getWorkEmail());
		user.setIsActive(true);

		String tempPassword = CommonModuleUtils.generateSecureRandomPassword();
		Optional<User> firstUser = userDao.findById(1L);
		LoginMethod loginMethod = firstUser.isPresent() ? firstUser.get().getLoginMethod() : LoginMethod.CREDENTIALS;

		user.setLoginMethod(loginMethod);

		if (loginMethod.equals(LoginMethod.GOOGLE)) {
			user.setIsPasswordChangedForTheFirstTime(true);
		}
		else {
			user.setTempPassword(encryptionDecryptionService.encrypt(tempPassword, encryptSecret));
			user.setPassword(passwordEncoder.encode(tempPassword));
			user.setIsPasswordChangedForTheFirstTime(false);
		}

		user.setEmployee(finalEmployee);
		finalEmployee.setUser(user);
		userDao.save(user);

		List<EmployeeProgressionsDto> progressions = employeeDetailsDto.getEmployeeProgressions();
		if (progressions != null && !progressions.isEmpty()) {
			for (EmployeeProgressionsDto progression : progressions) {
				if (Boolean.TRUE.equals(progression.getIsCurrent())) {
					JobFamily jobFamily = jobFamilyDao.getJobFamilyById(progression.getJobFamilyId());
					JobTitle jobTitle = jobTitleDao.getJobTitleById(progression.getJobTitleId());

					finalEmployee.setJobFamily(jobFamily);
					finalEmployee.setJobTitle(jobTitle);

					employeeDao.save(finalEmployee);
				}
			}
		}

		Set<EmployeeManager> managers = addNewManagers(employeeDetailsDto, finalEmployee);
		finalEmployee.setManagers(managers);

		employeeDao.save(finalEmployee);

		employeeTimelineService.addNewEmployeeTimeLineRecords(finalEmployee, employeeDetailsDto);
		rolesService.assignRolesToEmployee(employeeDetailsDto.getUserRoles(), finalEmployee);

		EmployeeDetailedResponseDto employeeResponseDto = peopleMapper
			.employeeToEmployeeDetailedResponseDto(finalEmployee);
		if (employeeDetailsDto.getProbationPeriod() != null) {
			employeeResponseDto.setPeriodResponseDto(peopleMapper.employeePeriodToEmployeePeriodResponseDto(
					saveEmployeePeriod(finalEmployee, employeeDetailsDto.getProbationPeriod())));
		}

		EmployeeCredentialsResponseDto employeeCredentials = new EmployeeCredentialsResponseDto();
		employeeCredentials.setEmail(finalEmployee.getUser().getEmail());

		if (loginMethod.equals(LoginMethod.CREDENTIALS)) {
			employeeCredentials.setTempPassword(tempPassword);
			employeeResponseDto.setEmployeeCredentials(employeeCredentials);
		}

		peopleEmailService.sendUserInvitationEmail(user);

		return new ResponseEntityDto(false, employeeResponseDto);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ResponseEntityDto quickAddEmployee(EmployeeQuickAddDto employeeQuickAddDto) {
		User currentUser = userService.getCurrentUser();
		log.info("quickAddEmployee: execution started by user: {}", currentUser.getUserId());

		Optional<User> existingUser = userDao.findByEmail(employeeQuickAddDto.getWorkEmail());
		if (existingUser.isPresent()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_USER_EMAIL_ALREADY_EXIST);
		}

		validateRoles(employeeQuickAddDto.getUserRoles());

		Employee finalEmployee = peopleMapper.employeeQuickAddDtoToEmployee(employeeQuickAddDto);
		finalEmployee.setAccountStatus(AccountStatus.PENDING);
		User user = new User();
		user.setEmail(employeeQuickAddDto.getWorkEmail());
		user.setIsActive(true);
		String tempPassword = CommonModuleUtils.generateSecureRandomPassword();

		user.setTempPassword(encryptionDecryptionService.encrypt(tempPassword, encryptSecret));
		user.setPassword(passwordEncoder.encode(tempPassword));

		User firstUser = userDao.findById(1L)
			.orElseThrow(() -> new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND));
		LoginMethod loginMethod = firstUser.getLoginMethod();

		if (loginMethod.equals(LoginMethod.GOOGLE)) {
			user.setIsPasswordChangedForTheFirstTime(true);
			user.setLoginMethod(LoginMethod.GOOGLE);
		}
		else {
			user.setIsPasswordChangedForTheFirstTime(false);
			user.setLoginMethod(LoginMethod.CREDENTIALS);
		}

		finalEmployee.setUser(user);
		user.setEmployee(finalEmployee);

		userDao.save(user);
		employeeDao.save(finalEmployee);

		rolesService.assignRolesToEmployee(employeeQuickAddDto.getUserRoles(), finalEmployee);

		peopleEmailService.sendUserInvitationEmail(finalEmployee.getUser());

		EmployeeDetailedResponseDto employeeResponseDto = peopleMapper
			.employeeToEmployeeDetailedResponseDto(finalEmployee);

		EmployeeCredentialsResponseDto employeeCredentials = new EmployeeCredentialsResponseDto();
		employeeCredentials.setEmail(finalEmployee.getUser().getEmail());
		employeeCredentials.setTempPassword(tempPassword);

		employeeResponseDto.setEmployeeCredentials(employeeCredentials);

		log.info("quickAddEmployee: execution ended by user: {}", currentUser.getUserId());
		return new ResponseEntityDto(false, employeeResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto updateEmployee(Long employeeId, EmployeeUpdateDto employeeUpdateDto) {
		log.info("updateEmployee: execution started");

		Optional<Employee> optionalEmployee = employeeDao.findById(employeeId);
		if (optionalEmployee.isEmpty()) {
			log.info("updateEmployee: employee with ID {} not found", employeeId);
			throw new EntityNotFoundException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		validateRoles(employeeUpdateDto.getUserRoles());

		String employeePreviousName = optionalEmployee.get().getFirstName();
		String employeePreviousLastName = optionalEmployee.get().getLastName();

		Employee employee = optionalEmployee.get();
		rolesService.updateEmployeeRoles(employeeUpdateDto.getUserRoles(), employee);

		List<EmployeeTimeline> employeeTimelines = new ArrayList<>();
		processAndUpdateEmployeeDetails(employeeUpdateDto, employee, employeeTimelines);

		updateManagers(employeeUpdateDto, employee);

		List<EmployeeProgressionsDto> progressions = employeeUpdateDto.getEmployeeProgressions();
		if (progressions != null && !progressions.isEmpty()) {
			for (EmployeeProgressionsDto progression : progressions) {
				if (Boolean.TRUE.equals(progression.getIsCurrent())) {
					JobFamily jobFamily = jobFamilyDao.getJobFamilyById(progression.getJobFamilyId());
					JobTitle jobTitle = jobTitleDao.getJobTitleById(progression.getJobTitleId());

					employee.setJobFamily(jobFamily);
					employee.setJobTitle(jobTitle);
				}
			}
		}

		employee = employeeDao.save(employee);
		employeeTimelineDao.saveAll(employeeTimelines);
		modifyManagerEmployeesHistory(employeePreviousName, employeePreviousLastName, employee);
		EmployeeDetailedResponseDto responseDto = peopleMapper.employeeToEmployeeDetailedResponseDto(employee);
		setEmployeePeriodDto(responseDto);

		log.info("updateEmployee: execution ended");
		return new ResponseEntityDto(false, responseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto getEmployees(EmployeeFilterDto employeeFilterDto) {
		User currentUser = userService.getCurrentUser();
		log.info("getEmployees: execution started by user: {}", currentUser.getUserId());
		int pageSize = employeeFilterDto.getSize();

		boolean isExport = employeeFilterDto.getIsExport();
		if (isExport) {
			pageSize = (int) employeeDao.count();
		}

		Pageable pageable = PageRequest.of(employeeFilterDto.getPage(), pageSize,
				Sort.by(employeeFilterDto.getSortOrder(), employeeFilterDto.getSortKey().toString()));

		Page<Employee> employees = employeeDao.findEmployees(employeeFilterDto, pageable);
		PageDto pageDto = pageTransformer.transform(employees);

		List<Long> employeeIds = employees.stream().map(Employee::getEmployeeId).toList();
		List<EmployeeTeamDto> teamList = employeeDao.findTeamsByEmployees(employeeIds);
		if (!isExport) {
			pageDto.setItems(fetchEmployeeSearchData(employees));
			log.info("getEmployees: Successfully finished returning {} employees",
					((List<?>) pageDto.getItems()).size());
			return new ResponseEntityDto(false, pageDto);
		}
		else {
			List<EmployeeDataExportResponseDto> responseDtos = exportEmployeeData(employees, teamList, employeeIds);
			log.info("getEmployees: Successfully finished returning {} employees on exportEmployeeData",
					responseDtos.size());
			return new ResponseEntityDto(false, responseDtos);
		}
	}

	@Override
	@Transactional
	public ResponseEntityDto getEmployeeById(Long employeeId) {
		User currentUser = userService.getCurrentUser();

		Boolean isPeopleOrSuperAdmin = currentUser.getEmployee().getEmployeeRole().getIsSuperAdmin()
				|| currentUser.getEmployee().getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_ADMIN)
				|| currentUser.getEmployee().getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_MANAGER);

		Boolean isAttendanceAdminOrManager = (!currentUser.getEmployee().getEmployeeRole().getIsSuperAdmin()
				&& !currentUser.getEmployee().getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_ADMIN))
				&& (currentUser.getEmployee().getEmployeeRole().getAttendanceRole().equals(Role.ATTENDANCE_MANAGER)
						|| currentUser.getEmployee()
							.getEmployeeRole()
							.getAttendanceRole()
							.equals(Role.ATTENDANCE_ADMIN));

		Boolean isLeaveAdminOrManager = (!currentUser.getEmployee().getEmployeeRole().getIsSuperAdmin()
				&& !currentUser.getEmployee().getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_ADMIN))
				&& (currentUser.getEmployee().getEmployeeRole().getAttendanceRole().equals(Role.LEAVE_MANAGER)
						|| currentUser.getEmployee().getEmployeeRole().getAttendanceRole().equals(Role.LEAVE_ADMIN));

		log.info("getEmployeeById: execution started by user: {}", currentUser.getUserId());
		Optional<Employee> employeeOptional = employeeDao.findById(employeeId);
		if (employeeOptional.isEmpty()) {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND);
		}
		Employee employee = employeeOptional.get();

		if (Boolean.TRUE.equals(isPeopleOrSuperAdmin)) {
			ManagerEmployeeDto managerEmployeeDto = peopleMapper.employeeToManagerEmployeeDto(employee);
			Optional<EmployeePeriod> period = employeePeriodDao
				.findEmployeePeriodByEmployee_EmployeeId(employee.getEmployeeId());

			List<EmployeeProgressionResponseDto> progressionResponseDtos = employee.getEmployeeProgressions()
				.stream()
				.map(this::mapToEmployeeProgressionResponseDto)
				.collect(Collectors.toList());

			managerEmployeeDto.setEmployeeProgressions(progressionResponseDtos);

			List<ManagingEmployeesResponseDto> managers = new ArrayList<>();

			employee.getManagers().forEach(employeeManager -> {
				ManagingEmployeesResponseDto emp = new ManagingEmployeesResponseDto();
				emp.setEmployee(peopleMapper.employeeToManagerCoreDetailsDto(employeeManager.getEmployee()));
				emp.setManagerType(employeeManager.getManagerType());
				emp.setIsPrimaryManager(employeeManager.isPrimaryManager());

				managers.add(emp);
			});

			managerEmployeeDto.setManagers(managers);

			List<TeamEmployeeResponseDto> teams = new ArrayList<>();

			setEmployeeTeams(teams, employee);
			managerEmployeeDto.setTeams(teams);
			managerEmployeeDto
				.setUserRoles(peopleMapper.employeeRoleToEmployeeRoleResponseDto(employee.getEmployeeRole()));
			if (period.isPresent()) {
				EmployeePeriodResponseDto periodResponseDto = peopleMapper
					.employeePeriodToEmployeePeriodResponseDto(period.get());
				managerEmployeeDto.setPeriodResponseDto(periodResponseDto);
			}

			log.info("getEmployeeById: Successfully finished returning employee data");
			return new ResponseEntityDto(false, managerEmployeeDto);
		}
		else if (Boolean.TRUE.equals(isAttendanceAdminOrManager) || Boolean.TRUE.equals(isLeaveAdminOrManager)) {
			SummarizedManagerEmployeeDto summarizedManagerEmployeeDto = peopleMapper
				.employeeToSummarizedManagerEmployeeDto(employee);
			List<TeamEmployeeResponseDto> teams = new ArrayList<>();
			setEmployeeTeams(teams, employee);
			summarizedManagerEmployeeDto.setTeams(teams);

			log.info("getEmployeeById: Successfully finished returning employee data for managers");
			return new ResponseEntityDto(false, summarizedManagerEmployeeDto);
		}
		else {

			SummarizedEmployeeDtoForEmployees summarizedEmployeeDtoForEmployees = peopleMapper
				.employeeToSummarizedEmployeeDtoForEmployees(employee);
			List<TeamEmployeeResponseDto> teams = new ArrayList<>();
			setEmployeeTeams(teams, employee);
			summarizedEmployeeDtoForEmployees.setTeams(teams);

			log.info("getEmployeeById: Successfully finished returning employee data for employee");
			return new ResponseEntityDto(false, summarizedEmployeeDtoForEmployees);
		}
	}

	private EmployeeProgressionResponseDto mapToEmployeeProgressionResponseDto(EmployeeProgression progression) {
		EmployeeProgressionResponseDto responseDto = new EmployeeProgressionResponseDto();

		responseDto.setProgressionId(progression.getProgressionId());
		responseDto.setEmployeeType(progression.getEmployeeType());
		responseDto.setStartDate(progression.getStartDate());
		responseDto.setEndDate(progression.getEndDate());

		if (progression.getJobFamilyId() != null) {
			JobFamily jobFamily = jobFamilyDao.getJobFamilyById(progression.getJobFamilyId());
			EmployeeJobFamilyDto jobFamilyDto = peopleMapper.jobFamilyToEmployeeJobFamilyDto(jobFamily);
			responseDto.setJobFamily(jobFamilyDto);
		}

		if (progression.getJobTitleId() != null) {
			JobTitle jobTitle = jobTitleDao.getJobTitleById(progression.getJobTitleId());
			JobTitleDto jobTitleDto = peopleMapper.jobTitleToJobTitleDto(jobTitle);
			responseDto.setJobTitle(jobTitleDto);
		}

		return responseDto;
	}

	public void setEmployeeTeams(List<TeamEmployeeResponseDto> teams, Employee employee) {
		employee.getTeams().forEach(employeeTeam -> {
			TeamEmployeeResponseDto teamEmployeeResponseDto = new TeamEmployeeResponseDto();
			TeamDetailResponseDto team = new TeamDetailResponseDto();
			team.setIsSupervisor(employeeTeam.getIsSupervisor());
			team.setTeamName(employeeTeam.getTeam().getTeamName());
			team.setTeamId(employeeTeam.getTeam().getTeamId());
			teamEmployeeResponseDto.setTeam(team);
			teams.add(teamEmployeeResponseDto);
		});
	}

	@Override
	@Transactional
	public ResponseEntityDto getCurrentEmployee() {
		User user = userService.getCurrentUser();
		Optional<Employee> employee = employeeDao.findById(user.getUserId());
		if (employee.isEmpty()) {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND);
		}
		EmployeeDetailedResponseDto employeeDetailedResponseDto = peopleMapper
			.employeeToEmployeeDetailedResponseDto(employee.get());
		Optional<EmployeePeriod> period = employeePeriodDao
			.findEmployeePeriodByEmployee_EmployeeId(employee.get().getEmployeeId());

		if (employee.get().getEmployeeRole() != null) {
			employeeDetailedResponseDto
				.setEmployeeRole(peopleMapper.employeeRoleToEmployeeRoleResponseDto(employee.get().getEmployeeRole()));
		}
		if (period.isPresent()) {
			EmployeePeriodResponseDto periodResponseDto = peopleMapper
				.employeePeriodToEmployeePeriodResponseDto(period.get());
			employeeDetailedResponseDto.setPeriodResponseDto(periodResponseDto);
		}
		else {
			log.info("No employee period found");
		}
		return new ResponseEntityDto(false, employeeDetailedResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto addBulkEmployees(List<EmployeeBulkDto> employeeBulkDtoList) {
		User currentUser = userService.getCurrentUser();
		log.info("addEmployeeBulk: execution started by user: {}", currentUser.getUserId());

		ExecutorService executorService = Executors.newFixedThreadPool(6);
		List<EmployeeBulkResponseDto> results = Collections.synchronizedList(new ArrayList<>());
		AtomicReference<ResponseEntityDto> outValues = new AtomicReference<>(new ResponseEntityDto());

		List<CompletableFuture<Void>> tasks = createEmployeeTasks(employeeBulkDtoList, executorService, results);
		waitForTaskCompletion(tasks, executorService);

		asyncEmailServiceImpl.sendEmailsInBackground(results);

		generateBulkErrorResponse(outValues, employeeBulkDtoList.size(), results);
		return outValues.get();
	}

	@Override
	@Transactional
	public ResponseEntityDto getLoginPendingEmployeeCount() {
		User currentUser = userService.getCurrentUser();
		log.info("getLoginPendingEmployeeCount: execution started by user: {}", currentUser.getUserId());

		EmployeeCountDto employeeCount = employeeDao.getLoginPendingEmployeeCount();
		if (employeeCount == null) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_LOGIN_PENDING_EMPLOYEES_NOT_FOUND);
		}
		return new ResponseEntityDto(false, employeeCount);
	}

	@Override
	@Transactional
	public ResponseEntityDto searchEmployeesByNameOrEmail(PermissionFilterDto permissionFilterDto) {
		log.info("searchEmployeesByNameOrEmail: execution started");

		List<Employee> employees = employeeDao.findEmployeeByNameEmail(permissionFilterDto.getKeyword(),
				permissionFilterDto);
		List<EmployeeDetailedResponseDto> employeeResponseDtos = peopleMapper
			.employeeListToEmployeeDetailedResponseDtoList(employees);

		log.info("searchEmployeesByNameOrEmail: execution ended");
		return new ResponseEntityDto(false, employeeResponseDtos);
	}

	@Override
	@Transactional
	public ResponseEntityDto searchEmployeesByEmail(String email) {
		log.info("searchEmployeesByEmail: execution started");
		Validations.validateEmail(email);
		Boolean isValidEmail = (employeeDao.findEmployeeByEmail(email) != null);
		log.info("searchEmployeesByEmail: execution ended");
		return new ResponseEntityDto(false, isValidEmail);
	}

	@Override
	@Transactional
	public ResponseEntityDto getEmployeeByIdOrEmail(EmployeeDataValidationDto employeeDataValidationDto) {
		User currentUser = userService.getCurrentUser();
		log.info("getEmployeeByIdOrEmail: execution started by user: {}", currentUser.getUserId());

		String workEmailCheck = employeeDataValidationDto.getWorkEmail();
		String identificationNoCheck = employeeDataValidationDto.getIdentificationNo();
		Optional<User> newUser = userDao.findByEmail(workEmailCheck);
		List<Employee> newEmployees = employeeDao.findByIdentificationNo(identificationNoCheck);
		EmployeeDataValidationResponseDto employeeDataValidationResponseDto = new EmployeeDataValidationResponseDto();
		employeeDataValidationResponseDto.setIsWorkEmailExists(newUser.isPresent());
		String userDomain = workEmailCheck.substring(workEmailCheck.indexOf("@") + 1);
		employeeDataValidationResponseDto.setIsGoogleDomain(Validation.ssoTypeMatches(userDomain));

		if (!newEmployees.isEmpty()) {
			employeeDataValidationResponseDto.setIsIdentificationNoExists(true);
		}
		return new ResponseEntityDto(false, employeeDataValidationResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto updateLoggedInUser(Long employeeId, EmployeeUpdateDto employeeUpdateDto) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.getEmployee().getEmployeeId().equals(employeeId)) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_USER_ID_DOES_NOT_MATCH);
		}
		else {
			log.info("updateLoggedInUser: execution started by user: {}", currentUser.getUserId());
			Optional<Employee> employeeResult = employeeDao.findById(employeeId);
			if (employeeResult.isEmpty()) {
				log.info("updateLoggedInUser: employee with ID {} not found", employeeId);
				throw new EntityNotFoundException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
			}

			Employee employee = employeeResult.get();

			if (employeeUpdateDto.getIdentificationNo() != null) {
				if (Validations.isValidIdentificationNo(employeeUpdateDto.getIdentificationNo())) {
					employee.setIdentificationNo(employeeUpdateDto.getIdentificationNo());
				}
				else {
					throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_IDENTIFICATION_NUMBER);
				}
			}

			updateLoggedInUserGeneralDetails(employeeUpdateDto, employee);
			updateLoggedInUserPersonalDetails(employeeUpdateDto, employee);
			updateLoggedInUserFamilyDetails(employeeUpdateDto, employee);
			updateLoggedInUserEducationalDetails(employeeUpdateDto, employee);
			updateLoggedInUserVisaDetails(employeeUpdateDto, employee);
			updateLoggedInUserEmergencyDetails(employeeUpdateDto, employee);

			employee = employeeDao.save(employee);
			EmployeeResponseDto responseDto = peopleMapper.employeeToEmployeeResponseDto(employee);
			return new ResponseEntityDto(false, responseDto);
		}
	}

	@Override
	@Transactional
	public ResponseEntityDto terminateUser(Long userId) {
		log.info("updateUserStatus: execution started");

		Optional<User> optionalUser = userDao.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		User user = optionalUser.get();

		if (!Boolean.TRUE.equals(user.getIsActive())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_ACCOUNT_DEACTIVATED);
		}

		List<Team> teamsManagedByUser = teamDao.findTeamsManagedByUser(user.getUserId(), true);
		if (!teamsManagedByUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_TEAM_EMPLOYEE_SUPERVISING_TEAMS);
		}

		Long supervisingEmployees = employeeDao.countEmployeesByManagerId(user.getUserId());
		if (supervisingEmployees > 0) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EMPLOYEE_SUPERVISING_EMPLOYEES);
		}

		Employee employee = user.getEmployee();
		employee.setJobTitle(null);
		employee.setJobFamily(null);

		List<EmployeeTeam> employeeTeams = employeeTeamDao.findEmployeeTeamsByEmployee(employee);
		employeeTeamDao.deleteAll(employeeTeams);
		employee.setTeams(null);

		user.setIsActive(false);
		user.getEmployee().setAccountStatus(AccountStatus.TERMINATED);
		user.getEmployee().setTerminationDate(DateTimeUtils.getCurrentUtcDate());

		peopleEmailService.sendUserTerminationEmail(user);

		userDao.save(user);
		employeeDao.save(employee);

		log.info("updateUserStatus: execution ended");
		return new ResponseEntityDto(false, "User status updated successfully");
	}

	@Override
	@Transactional
	public List<EmployeeManagerResponseDto> getCurrentEmployeeManagers() {
		User user = userService.getCurrentUser();

		List<EmployeeManager> employeeManagers = employeeManagerDao.findByEmployee(user.getEmployee());
		return employeeManagers.stream().map(employeeManager -> {
			EmployeeManagerResponseDto responseDto = new EmployeeManagerResponseDto();
			Employee manager = employeeManager.getManager();

			responseDto.setEmployeeId(manager.getEmployeeId());
			responseDto.setFirstName(manager.getFirstName());
			responseDto.setLastName(manager.getLastName());
			responseDto.setMiddleName(manager.getMiddleName());
			responseDto.setAuthPic(manager.getAuthPic());
			responseDto.setIsPrimaryManager(employeeManager.isPrimaryManager());
			responseDto.setManagerType(employeeManager.getManagerType());

			return responseDto;
		}).toList();
	}

	@Override
	public ResponseEntityDto updateNotificationSettings(
			NotificationSettingsPatchRequestDto notificationSettingsPatchRequestDto) {
		log.info("updateNotificationSettings: execution started");

		User currentUser = userService.getCurrentUser();
		Optional<User> optionalUser = userDao.findById(currentUser.getUserId());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		User user = optionalUser.get();

		UserSettings userSettings;
		if (user.getSettings() != null) {
			userSettings = user.getSettings();
		}
		else {
			userSettings = new UserSettings();
			userSettings.setUser(user);
			user.setSettings(userSettings);
		}

		ObjectNode notificationsObjectNode = mapper.createObjectNode();

		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST.getKey(),
				notificationSettingsPatchRequestDto.getIsLeaveRequestNotificationsEnabled());
		notificationsObjectNode.put(NotificationSettingsType.TIME_ENTRY.getKey(),
				notificationSettingsPatchRequestDto.getIsTimeEntryNotificationsEnabled());
		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST_NUDGE.getKey(),
				notificationSettingsPatchRequestDto.getIsLeaveRequestNudgeNotificationsEnabled());

		userSettings.setNotifications(notificationsObjectNode);
		user.setSettings(userSettings);

		userDao.save(user);

		log.info("updateNotificationSettings: execution ended");
		return new ResponseEntityDto(true, "Notification settings updated successfully");
	}

	@Override
	public ResponseEntityDto getNotificationSettings() {
		log.info("getNotificationSettings: execution started");

		User currentUser = userService.getCurrentUser();
		Optional<User> optionalUser = userDao.findById(currentUser.getUserId());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		UserSettings userSettings = currentUser.getSettings();
		NotificationSettingsResponseDto userSettingsResponseDto = new NotificationSettingsResponseDto();

		if (userSettings != null && userSettings.getNotifications() != null) {
			JsonNode notifications = userSettings.getNotifications();

			userSettingsResponseDto.setIsLeaveRequestNotificationsEnabled(
					notifications.has(NotificationSettingsType.LEAVE_REQUEST.getKey())
							&& notifications.get(NotificationSettingsType.LEAVE_REQUEST.getKey()).asBoolean());
			userSettingsResponseDto
				.setIsTimeEntryNotificationsEnabled(notifications.has(NotificationSettingsType.TIME_ENTRY.getKey())
						&& notifications.get(NotificationSettingsType.TIME_ENTRY.getKey()).asBoolean());
			userSettingsResponseDto.setIsLeaveRequestNudgeNotificationsEnabled(
					notifications.has(NotificationSettingsType.LEAVE_REQUEST_NUDGE.getKey())
							&& notifications.get(NotificationSettingsType.LEAVE_REQUEST_NUDGE.getKey()).asBoolean());
		}
		else {
			userSettingsResponseDto.setIsLeaveRequestNotificationsEnabled(false);
			userSettingsResponseDto.setIsTimeEntryNotificationsEnabled(false);
			userSettingsResponseDto.setIsLeaveRequestNudgeNotificationsEnabled(false);
		}

		log.info("getNotificationSettings: execution ended");
		return new ResponseEntityDto(true, userSettingsResponseDto);
	}

	@Override
	public boolean isManagerAvailableForCurrentEmployee() {
		User user = userService.getCurrentUser();
		return employeeManagerDao.existsByEmployee(user.getEmployee());
	}

	@Override
	@Transactional
	public ResponseEntityDto searchEmployeesAndTeamsByKeyword(String keyword) {
		User currentUser = userService.getCurrentUser();
		log.info("searchEmployeesAndTeamsByKeyword: execution started by user: {} to search users by the keyword {}",
				currentUser.getUserId(), keyword);

		List<Team> teams = teamDao.findTeamsByName(keyword);
		List<Employee> employees = employeeDao.findEmployeeByName(keyword);

		AnalyticsSearchResponseDto analyticsSearchResponseDto = new AnalyticsSearchResponseDto(
				peopleMapper.employeeListToEmployeeSummarizedResponseDto(employees),
				peopleMapper.teamToTeamDetailResponseDto(teams));

		log.info("searchEmployeesAndTeamsByKeyword: execution ended by user: {} to search users by the keyword {}",
				currentUser.getUserId(), keyword);
		return new ResponseEntityDto(false, analyticsSearchResponseDto);
	}

	private void updateLoggedInUserGeneralDetails(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getAuthPic() != null) {
			employee.setAuthPic(employeeUpdateDto.getAuthPic());
		}
		if (employeeUpdateDto.getFirstName() != null
				&& Validations.isEmployeeNameValid(employeeUpdateDto.getFirstName())
				&& !employeeUpdateDto.getFirstName().isBlank()) {
			employee.setFirstName(employeeUpdateDto.getFirstName());
		}
		if (employeeUpdateDto.getLastName() != null && Validations.isEmployeeNameValid(employeeUpdateDto.getLastName())
				&& !employeeUpdateDto.getLastName().isBlank()) {
			employee.setLastName(employeeUpdateDto.getLastName());
		}
		if (employeeUpdateDto.getMiddleName() != null && !employeeUpdateDto.getMiddleName().isBlank()
				&& Validations.isEmployeeNameValid(employeeUpdateDto.getMiddleName())) {
			employee.setMiddleName(employeeUpdateDto.getMiddleName());
		}
		else if (employeeUpdateDto.getMiddleName() != null && !employeeUpdateDto.getMiddleName().isBlank()) {
			employee.setMiddleName(null);
		}
		if (employeeUpdateDto.getAddress() != null && !employeeUpdateDto.getAddress().isBlank()) {
			employee.setAddress(employeeUpdateDto.getAddress());
		}
		if (employeeUpdateDto.getAddressLine2() != null && !employeeUpdateDto.getAddressLine2().isBlank()) {
			employee.setAddressLine2(employeeUpdateDto.getAddressLine2());
		}
		if (employeeUpdateDto.getPersonalEmail() != null && !employeeUpdateDto.getPersonalEmail().isBlank()) {
			employee.setPersonalEmail(employeeUpdateDto.getPersonalEmail());
		}
		if (employeeUpdateDto.getGender() != null) {
			employee.setGender(employeeUpdateDto.getGender());
		}
		if (employeeUpdateDto.getPhone() != null && !employeeUpdateDto.getPhone().isBlank()) {
			employee.setPhone(employeeUpdateDto.getPhone());
		}
		if (employeeUpdateDto.getCountry() != null && !employeeUpdateDto.getCountry().isBlank()) {
			employee.setCountry(employeeUpdateDto.getCountry());
		}
		if (employeeUpdateDto.getEeo() != null) {
			employee.setEeo(employeeUpdateDto.getEeo());
		}
		if (employeeUpdateDto.getTimeZone() != null && !employeeUpdateDto.getTimeZone().isBlank()) {
			employee.setTimeZone(employeeUpdateDto.getTimeZone());
		}
	}

	private void updateLoggedInUserEmergencyDetails(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeEmergency() != null) {
			List<EmployeeEmergency> emergencies = employee.getEmployeeEmergencies();
			for (EmployeeEmergencyDto employeeEmergencyDto : employeeUpdateDto.getEmployeeEmergency()) {
				updateLoggedInUserEmergencyDetailsFromDto(employeeEmergencyDto, emergencies, employee);
			}
		}
	}

	private void updateLoggedInUserEmergencyDetailsFromDto(EmployeeEmergencyDto employeeEmergencyDto,
			List<EmployeeEmergency> emergencies, Employee employee) {
		EmployeeEmergency emergency = null;
		if (employeeEmergencyDto.getEmergencyId() != null) {
			for (EmployeeEmergency existingEmergency : emergencies) {
				if (existingEmergency.getEmergencyId().equals(employeeEmergencyDto.getEmergencyId())) {
					emergency = existingEmergency;
					break;
				}
			}
		}
		else {
			emergency = new EmployeeEmergency();
			emergency.setEmployee(employee);
			emergencies.add(emergency);
		}
		if (emergency != null) {
			setEmergencyDetails(emergency, employeeEmergencyDto);
		}

	}

	private void setEmergencyDetails(EmployeeEmergency emergency, EmployeeEmergencyDto dto) {
		emergency.setName(dto.getName() != null ? dto.getName() : emergency.getName());
		emergency.setEmergencyRelationship(dto.getEmergencyRelationship() != null ? dto.getEmergencyRelationship()
				: emergency.getEmergencyRelationship());
		emergency.setContactNo(dto.getContactNo() != null ? dto.getContactNo() : emergency.getContactNo());
		emergency.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : emergency.getIsPrimary());
	}

	private void updateLoggedInUserVisaDetails(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeVisas() != null) {
			List<EmployeeVisa> visas = employee.getEmployeeVisas();
			Validations.validateVisaDates(employeeUpdateDto.getEmployeeVisas());
			List<Long> currentIdList = employee.getEmployeeVisas().stream().map(EmployeeVisa::getVisaId).toList();
			List<Long> updatingIdList = new ArrayList<>();
			updateLoggedInUserVisaFromDto(employeeUpdateDto, updatingIdList, visas, employee);
			if (!currentIdList.isEmpty() && currentIdList.size() > updatingIdList.size()) {
				for (Long item : currentIdList) {
					if (!updatingIdList.contains(item)) {
						removeUnusedVisaDetails(item, employee);
					}
				}
			}
		}
	}

	private void removeUnusedVisaDetails(Long id, Employee employee) {
		Optional<EmployeeVisa> visaOptional = employeeVisaDao.findByVisaId(id);
		if (visaOptional.isPresent()) {
			employeeVisaDao.deleteById(visaOptional.get().getVisaId());
			employee.getEmployeeVisas().remove(visaOptional.get());
		}
	}

	private void updateLoggedInUserVisaFromDto(EmployeeUpdateDto employeeUpdateDto, List<Long> updatingIdList,
			List<EmployeeVisa> visas, Employee employee) {
		for (EmploymentVisaDto employeeVisaDto : employeeUpdateDto.getEmployeeVisas()) {
			setCurrentUserSetVisaDetails(employeeVisaDto, visas, updatingIdList, employee);
		}
	}

	private void setCurrentUserSetVisaDetails(EmploymentVisaDto employeeVisaDto, List<EmployeeVisa> visas,
			List<Long> updatingIdList, Employee employee) {
		EmployeeVisa visa = null;
		if (employeeVisaDto.getVisaId() != null) {
			for (EmployeeVisa existingVisa : visas) {
				if (existingVisa.getVisaId().equals(employeeVisaDto.getVisaId())) {
					visa = existingVisa;
					updatingIdList.add(existingVisa.getVisaId());
					break;
				}
			}
		}
		else {
			visa = new EmployeeVisa();
			visa.setEmployee(employee);
			visas.add(visa);
		}

		if (visa != null) {
			setVisaDetails(visa, employeeVisaDto);
		}
	}

	private void setVisaDetails(EmployeeVisa visa, EmploymentVisaDto dto) {
		visa.setVisaType(dto.getVisaType() != null ? dto.getVisaType() : visa.getVisaType());
		visa.setIssuingCountry(dto.getIssuingCountry() != null ? dto.getIssuingCountry() : visa.getIssuingCountry());
		visa.setIssuedDate(dto.getIssuedDate() != null ? dto.getIssuedDate() : visa.getIssuedDate());
		visa.setExpirationDate(dto.getExpirationDate() != null ? dto.getExpirationDate() : visa.getExpirationDate());
	}

	private void updateLoggedInUserEducationalDetails(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeEducations() != null) {
			List<EmployeeEducation> employeeEducation = employee.getEmployeeEducations();
			List<Long> currentIdList = employee.getEmployeeEducations()
				.stream()
				.map(EmployeeEducation::getEducationId)
				.toList();
			List<Long> updatingIdList = new ArrayList<>();
			updateLoggedInUserEducationDetailsFromDto(employeeUpdateDto, employeeEducation, employee, updatingIdList);
			if (!currentIdList.isEmpty() && currentIdList.size() > updatingIdList.size()) {
				for (Long educationDetailsId : currentIdList) {
					if (!updatingIdList.contains(educationDetailsId)) {
						removeUnusedEducationDetails(educationDetailsId, employee);
					}
				}
			}
		}
	}

	private void removeUnusedEducationDetails(Long id, Employee employee) {
		Optional<EmployeeEducation> eduOptional = employeeEducationDao.findByEducationId(id);
		if (eduOptional.isPresent()) {
			employeeEducationDao.deleteById(eduOptional.get().getEducationId());
			employee.getEmployeeEducations().remove(eduOptional.get());
		}
	}

	private void updateLoggedInUserEducationDetailsFromDto(EmployeeUpdateDto employeeUpdateDto,
			List<EmployeeEducation> employeeEducation, Employee employee, List<Long> updatingIdList) {
		for (EmployeeEducationDto employeeEducationDto : employeeUpdateDto.getEmployeeEducations()) {
			setCurrentUserEducationDetailsExists(employeeEducation, employeeEducationDto, updatingIdList, employee);
		}
	}

	private void setCurrentUserEducationDetailsExists(List<EmployeeEducation> employeeEducation,
			EmployeeEducationDto employeeEducationDto, List<Long> updatingIdList, Employee employee) {
		EmployeeEducation education = null;
		if (employeeEducationDto.getEducationId() != null) {
			for (EmployeeEducation existingEducation : employeeEducation) {
				if (existingEducation.getEducationId().equals(employeeEducationDto.getEducationId())) {
					education = existingEducation;
					updatingIdList.add(existingEducation.getEducationId());
					break;
				}
			}
		}
		else {
			education = new EmployeeEducation();
			education.setEmployee(employee);
			employeeEducation.add(education);
		}

		if (education != null) {
			setEducationDetails(education, employeeEducationDto);
		}

	}

	private void setEducationDetails(EmployeeEducation education, EmployeeEducationDto dto) {
		education.setInstitution(dto.getInstitution() != null ? dto.getInstitution() : education.getInstitution());
		education.setDegree(dto.getDegree() != null ? dto.getDegree() : education.getDegree());
		education.setSpecialization(
				dto.getSpecialization() != null ? dto.getSpecialization() : education.getSpecialization());
		education.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : education.getStartDate());
		education.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : education.getEndDate());
	}

	private void updateLoggedInUserFamilyDetails(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeFamilies() != null) {
			List<EmployeeFamily> employeeFamilies = employee.getEmployeeFamilies();
			List<Long> currentIdList = employee.getEmployeeFamilies()
				.stream()
				.map(EmployeeFamily::getFamilyId)
				.toList();
			List<Long> updatingIdList = new ArrayList<>();
			updateLoggedInUserFamilyDetailsFromDto(employeeUpdateDto, employee, employeeFamilies, updatingIdList);
			if (!currentIdList.isEmpty() && currentIdList.size() > updatingIdList.size()) {
				for (Long familyId : currentIdList) {
					if (!updatingIdList.contains(familyId)) {
						removeUnusedFamilyDetails(familyId, employee);
					}
				}
			}
		}
	}

	private void removeUnusedFamilyDetails(Long id, Employee employee) {
		Optional<EmployeeFamily> famOptional = employeeFamilyDao.findByFamilyId(id);
		if (famOptional.isPresent()) {
			employeeFamilyDao.deleteById(famOptional.get().getFamilyId());
			employee.getEmployeeFamilies().remove(famOptional.get());
		}
	}

	private void updateLoggedInUserFamilyDetailsFromDto(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeFamily> employeeFamilies, List<Long> updatingIdList) {
		for (EmployeeFamilyDto employeeFamilyDto : employeeUpdateDto.getEmployeeFamilies()) {
			setCurrentUserFamilyDetailsExists(employeeFamilyDto, employeeFamilies, updatingIdList, employee);
		}
	}

	private void setCurrentUserFamilyDetailsExists(EmployeeFamilyDto employeeFamilyDto,
			List<EmployeeFamily> employeeFamilies, List<Long> updatingIdList, Employee employee) {
		EmployeeFamily family = null;
		if (employeeFamilyDto.getFamilyId() != null) {
			for (EmployeeFamily existingFamily : employeeFamilies) {
				if (existingFamily.getFamilyId().equals(employeeFamilyDto.getFamilyId())) {
					family = existingFamily;
					updatingIdList.add(existingFamily.getFamilyId());
					break;
				}
			}
		}
		else {
			family = new EmployeeFamily();
			family.setEmployee(employee);
			employeeFamilies.add(family);
		}
		if (family != null) {
			setFamilyDetails(family, employeeFamilyDto);
		}
	}

	private void setFamilyDetails(EmployeeFamily family, EmployeeFamilyDto dto) {
		family.setFirstName(dto.getFirstName() != null ? dto.getFirstName() : family.getFirstName());
		family.setLastName(dto.getLastName() != null ? dto.getLastName() : family.getLastName());
		family.setGender(dto.getGender() != null ? dto.getGender() : family.getGender());
		family.setBirthDate(dto.getBirthDate() != null ? dto.getBirthDate() : family.getBirthDate());
		family.setFamilyRelationship(
				dto.getFamilyRelationship() != null ? dto.getFamilyRelationship() : family.getFamilyRelationship());
		family.setParentName(dto.getParentName() != null ? dto.getParentName() : family.getParentName());
	}

	private void updateLoggedInUserPersonalDetails(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeePersonalInfo() == null) {
			return;
		}

		EmployeePersonalInfo personalInfo = getOrCreatePersonalInfo(employee);
		EmployeePersonalInfoDto dtoInfo = employeeUpdateDto.getEmployeePersonalInfo();

		setPersonalInfoFields(personalInfo, dtoInfo);
		employee.setPersonalInfo(personalInfo);
	}

	private EmployeePersonalInfo getOrCreatePersonalInfo(Employee employee) {
		EmployeePersonalInfo personalInfo = employee.getPersonalInfo();
		if (personalInfo == null) {
			personalInfo = new EmployeePersonalInfo();
			personalInfo.setEmployee(employee);
		}
		return personalInfo;
	}

	private void setPersonalInfoFields(EmployeePersonalInfo personalInfo, EmployeePersonalInfoDto dtoInfo) {
		setIfNotNull(dtoInfo.getBirthDate(), personalInfo::setBirthDate);
		setIfNotNull(dtoInfo.getNationality(), personalInfo::setNationality);
		setIfNotNull(dtoInfo.getNin(), personalInfo::setNin);
		setStringField(dtoInfo.getPassportNo(), personalInfo::setPassportNo);
		setIfNotNull(dtoInfo.getMaritalStatus(), personalInfo::setMaritalStatus);
		setIfNotNull(dtoInfo.getCity(), personalInfo::setCity);
		setIfNotNull(dtoInfo.getState(), personalInfo::setState);
		setStringField(dtoInfo.getPostalCode(), personalInfo::setPostalCode);
		setIfNotNull(dtoInfo.getExtraInfo(), personalInfo::setExtraInfo);
		setIfNotNull(dtoInfo.getBloodGroup(), personalInfo::setBloodGroup);
		setStringField(dtoInfo.getSsn(), personalInfo::setSsn);
		setIfNotNull(dtoInfo.getEthnicity(), personalInfo::setEthnicity);

		if (dtoInfo.getPreviousEmploymentDetails() != null) {
			validateEmploymentDates(dtoInfo.getPreviousEmploymentDetails());
			personalInfo.setPreviousEmploymentDetails(dtoInfo.getPreviousEmploymentDetails());
		}

		setIfNotNull(dtoInfo.getSocialMediaDetails(), personalInfo::setSocialMediaDetails);
	}

	private <T> void setIfNotNull(T value, Consumer<T> setter) {
		if (value != null) {
			setter.accept(value);
		}
	}

	private void setStringField(String value, Consumer<String> setter) {
		if (value != null) {
			setter.accept(value.isBlank() ? null : value);
		}
	}

	private List<CompletableFuture<Void>> createEmployeeTasks(List<EmployeeBulkDto> employeeBulkDtoList,
			ExecutorService executorService, List<EmployeeBulkResponseDto> results) {
		List<CompletableFuture<Void>> tasks = new ArrayList<>();
		List<List<EmployeeBulkDto>> chunkedEmployeeBulkData = CommonModuleUtils.chunkData(employeeBulkDtoList);
		TransactionTemplate transactionTemplate = getTransactionManagerTemplate();

		String tenant = bulkContextService.getContext();

		for (List<EmployeeBulkDto> employeeBulkChunkDtoList : chunkedEmployeeBulkData) {
			for (EmployeeBulkDto employeeBulkDto : employeeBulkChunkDtoList) {
				tasks.add(createEmployeeTask(employeeBulkDto, transactionTemplate, results, executorService, tenant));
			}
		}

		return tasks;
	}

	private CompletableFuture<Void> createEmployeeTask(EmployeeBulkDto employeeBulkDto,
			TransactionTemplate transactionTemplate, List<EmployeeBulkResponseDto> results,
			ExecutorService executorService, String tenant) {
		return CompletableFuture.runAsync(() -> {
			try {
				bulkContextService.setContext(tenant);
				saveEmployeeInTransaction(employeeBulkDto, transactionTemplate);
				handleSuccessResponse(employeeBulkDto,
						messageUtil.getMessage(PeopleMessageConstant.PEOPLE_SUCCESS_EMPLOYEE_ADDED), results);
			}
			catch (DataIntegrityViolationException e) {
				handleDataIntegrityException(employeeBulkDto, e, results);
			}
			catch (Exception e) {
				handleGeneralException(employeeBulkDto, e, results);
			}
		}, executorService);
	}

	private void saveEmployeeInTransaction(EmployeeBulkDto employeeBulkDto, TransactionTemplate transactionTemplate) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
				createNewEmployeeFromBulk(employeeBulkDto);
			}
		});
	}

	private void handleSuccessResponse(EmployeeBulkDto employeeBulkDto, String message,
			List<EmployeeBulkResponseDto> results) {
		log.warn("bulk employee added successfully : {}", employeeBulkDto.getWorkEmail());
		EmployeeBulkResponseDto bulkResponseDto = createSuccessResponse(employeeBulkDto, message);
		results.add(bulkResponseDto);
	}

	private void handleDataIntegrityException(EmployeeBulkDto employeeBulkDto, DataIntegrityViolationException e,
			List<EmployeeBulkResponseDto> results) {
		log.warn("addEmployeeBulk: data integrity violation exception occurred when saving : {}", e.getMessage());
		EmployeeBulkResponseDto bulkResponseDto = createErrorResponse(employeeBulkDto, e.getMessage());
		bulkResponseDto.setMessage(e.getMessage().contains("unique")
				? messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_DUPLICATE_IDENTIFICATION_NO)
				: e.getMessage());
		results.add(bulkResponseDto);
	}

	private void handleGeneralException(EmployeeBulkDto employeeBulkDto, Exception e,
			List<EmployeeBulkResponseDto> results) {
		log.warn("addEmployeeBulk: exception occurred when saving : {}", e.getMessage());
		EmployeeBulkResponseDto bulkResponseDto = createErrorResponse(employeeBulkDto, e.getMessage());
		results.add(bulkResponseDto);
	}

	private EmployeeBulkResponseDto createErrorResponse(EmployeeBulkDto employeeBulkDto, String message) {
		EmployeeBulkResponseDto bulkResponseDto = new EmployeeBulkResponseDto();
		bulkResponseDto.setEmail(employeeBulkDto.getWorkEmail() != null ? employeeBulkDto.getWorkEmail()
				: employeeBulkDto.getPersonalEmail());
		bulkResponseDto.setStatus(BulkItemStatus.ERROR);
		bulkResponseDto.setMessage(message);
		return bulkResponseDto;
	}

	private EmployeeBulkResponseDto createSuccessResponse(EmployeeBulkDto employeeBulkDto, String message) {
		EmployeeBulkResponseDto bulkResponseDto = new EmployeeBulkResponseDto();
		bulkResponseDto.setEmail(employeeBulkDto.getWorkEmail() != null ? employeeBulkDto.getWorkEmail()
				: employeeBulkDto.getPersonalEmail());
		bulkResponseDto.setStatus(BulkItemStatus.SUCCESS);
		bulkResponseDto.setMessage(message);
		return bulkResponseDto;
	}

	private void waitForTaskCompletion(List<CompletableFuture<Void>> tasks, ExecutorService executorService) {
		CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
		allTasks.thenRun(executorService::shutdown);
		allTasks.join();

		try {
			if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
				log.error("addEmployeeBulk: ExecutorService Failed to terminate after 5 minutes");
				log.error("addEmployeeBulk: Forcefully shutting down ExecutorService");
				List<Runnable> pendingTasks = executorService.shutdownNow();
				log.error("addEmployeeBulk: Found {} pending tasks while forcefully shutting down",
						pendingTasks.size());
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("addEmployeeBulk: Interrupted while waiting to terminate the ExecutorService", e);
		}
		catch (Exception e) {
			log.error("addEmployeeBulk: Error occurred while waiting to terminate the ExecutorService: {}",
					e.getMessage());
		}

		log.info("addEmployeeBulk: is executor shut down success : {}", executorService.isShutdown());
		log.info("addEmployeeBulk: all the tasks termination success after executor shut down : {}",
				executorService.isTerminated());
	}

	private void generateBulkErrorResponse(AtomicReference<ResponseEntityDto> outValues, int totalSize,
			List<EmployeeBulkResponseDto> results) {
		EmployeeBulkErrorResponseDto errorResponseDto = new EmployeeBulkErrorResponseDto();
		List<EmployeeBulkResponseDto> errorResults = results.stream()
			.filter(responseDto -> responseDto.getStatus() == BulkItemStatus.ERROR)
			.toList();
		errorResponseDto
			.setBulkStatusSummary(new BulkStatusSummary(totalSize - errorResults.size(), errorResults.size()));
		errorResponseDto.setBulkRecordErrorLogs(errorResults);
		outValues.set(new ResponseEntityDto(false, errorResponseDto));
	}

	private void createNewEmployeeFromBulk(EmployeeBulkDto employeeBulkDto) {
		List<String> validationErrors = validateEmployeeBulkDto(employeeBulkDto);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(
					PeopleMessageConstant.PEOPLE_ERROR_USER_ENTITLEMENT_BULK_UPLOAD_VALIDATION_FAILED,
					validationErrors);
		}

		if (employeeBulkDto.getIdentificationNo() != null)
			employeeBulkDto.setIdentificationNo(employeeBulkDto.getIdentificationNo().toUpperCase());
		Validations.isEmployeeNameValid(employeeBulkDto.getFirstName().concat(employeeBulkDto.getLastName()));

		Employee employee = peopleMapper.employeeBulkDtoToEmployee(employeeBulkDto);
		EmployeeDetailsDto employeeDetailsDto = peopleMapper.employeeBulkDtoToEmployeeDetailsDto(employeeBulkDto);

		User user = employee.getUser();
		user.setEmail(employeeBulkDto.getWorkEmail());
		user.setIsActive(true);

		User firstUser = userDao.findById(1L)
			.orElseThrow(() -> new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND));
		LoginMethod loginMethod = firstUser.getLoginMethod();

		if (loginMethod.equals(LoginMethod.GOOGLE)) {
			user.setIsPasswordChangedForTheFirstTime(true);
			user.setLoginMethod(LoginMethod.GOOGLE);
		}
		else {
			String tempPassword = CommonModuleUtils.generateSecureRandomPassword();

			user.setTempPassword(encryptionDecryptionService.encrypt(tempPassword, encryptSecret));
			user.setPassword(passwordEncoder.encode(tempPassword));
			user.setIsPasswordChangedForTheFirstTime(false);

			user.setIsPasswordChangedForTheFirstTime(false);
			user.setLoginMethod(LoginMethod.CREDENTIALS);
		}

		setBulkEmployeeProgression(employeeBulkDto, employee);
		setBulkManagers(employeeBulkDto, employeeDetailsDto);

		Set<EmployeeManager> managers = addNewManagers(employeeDetailsDto, employee);
		employee.setManagers(managers);

		if (employeeBulkDto.getEmployeeEmergency() != null && (employeeBulkDto.getEmployeeEmergency().getName() != null
				|| employeeBulkDto.getEmployeeEmergency().getContactNo() != null)) {
			EmployeeEmergency employeeEmergency = peopleMapper
				.employeeEmergencyDtoToEmployeeEmergency(employeeBulkDto.getEmployeeEmergency());
			employeeEmergency.setEmployee(employee);
			employee.setEmployeeEmergencies(List.of(employeeEmergency));
		}

		if (employeeDetailsDto.getEmployeePersonalInfo() != null) {
			EmployeePersonalInfo employeePersonalInfo = peopleMapper
				.employeePersonalInfoDtoToEmployeePersonalInfo(employeeDetailsDto.getEmployeePersonalInfo());
			employeePersonalInfo.setEmployee(employee);
			employee.setPersonalInfo(employeePersonalInfo);
		}

		employee.setAccountStatus(employeeBulkDto.getAccountStatus());
		employee.setEmploymentAllocation(employeeBulkDto.getEmploymentAllocation());

		UserSettings userSettings = createNotificationSettings(user);
		user.setSettings(userSettings);

		userDao.save(user);

		saveEmployeeRoles(employee);
		saveEmployeeProgression(employee, employeeBulkDto);

		if (!employeeBulkDto.getTeams().isEmpty()) {
			saveEmployeeTeams(employee, employeeBulkDto);
		}

		if (employeeBulkDto.getEmployeePeriod() != null) {
			saveEmployeePeriod(employee, employeeBulkDto.getEmployeePeriod());
		}

		EmployeeTimeline employeeTimeline = getEmployeeTimeline(employee, EmployeeTimelineType.JOINED_DATE,
				EmployeeTimelineConstant.TITLE_JOINED_DATE_CHANGED, null, String.valueOf(employee.getJoinDate()));
		employeeTimelineDao.save(employeeTimeline);
	}

	private void saveEmployeeTeams(Employee employee, EmployeeBulkDto employeeBulkDto) {
		if (employeeBulkDto.getTeams() != null) {
			Set<EmployeeTeam> employeeTeams = getEmployeeTeamsByName(employeeBulkDto.getTeams(), employee);
			employeeTeamDao.saveAll(employeeTeams);
		}
	}

	private void saveEmployeeProgression(Employee employee, EmployeeBulkDto employeeBulkDto) {
		if (employeeBulkDto.getJobFamily() != null || employeeBulkDto.getJobTitle() != null
				|| employeeBulkDto.getEmployeeType() != null) {
			List<EmployeeProgression> employeeProgressions = new ArrayList<>();
			EmployeeProgression employeeProgression = new EmployeeProgression();

			if (employeeBulkDto.getJobFamily() != null && !employeeBulkDto.getJobFamily().isEmpty()) {
				JobFamily jobFamily = jobFamilyDao.getJobFamilyByName(employeeBulkDto.getJobFamily());

				if (jobFamily != null) {
					employee.setJobFamily(jobFamily);
					employeeProgression.setJobFamilyId(jobFamily.getJobFamilyId());
				}
			}

			if (employeeBulkDto.getJobTitle() != null && !employeeBulkDto.getJobTitle().isEmpty()) {
				JobTitle jobTitle = jobTitleDao.getJobTitleByName(employeeBulkDto.getJobTitle());

				if (jobTitle != null) {
					employee.setJobTitle(jobTitle);
					employeeProgression.setJobTitleId(jobTitle.getJobTitleId());
				}
			}

			if (employeeBulkDto.getEmployeeType() != null && !employeeBulkDto.getEmployeeType().isEmpty()) {
				employeeProgression.setEmployeeType(EmployeeType.valueOf(employeeBulkDto.getEmployeeType()));
			}

			employeeProgression.setEmployee(employee);
			employeeProgressions.add(employeeProgression);
			employee.setEmployeeProgressions(employeeProgressions);

			employeeDao.save(employee);
		}
	}

	private UserSettings createNotificationSettings(User user) {
		log.info("createNotificationSettings: execution started");
		UserSettings userSettings = new UserSettings();

		EmployeeRolesRequestDto employeeRolesRequestDto = new EmployeeRolesRequestDto();
		employeeRolesRequestDto.setPeopleRole(Role.PEOPLE_EMPLOYEE);
		employeeRolesRequestDto.setLeaveRole(Role.LEAVE_EMPLOYEE);
		employeeRolesRequestDto.setAttendanceRole(Role.ATTENDANCE_EMPLOYEE);
		employeeRolesRequestDto.setIsSuperAdmin(false);

		ObjectNode notificationsObjectNode = mapper.createObjectNode();

		boolean isLeaveRequestNotificationsEnabled = true;
		boolean isTimeEntryNotificationsEnabled = true;
		boolean isNudgeNotificationsEnabled = employeeRolesRequestDto.isSuperAdmin
				|| employeeRolesRequestDto.getLeaveRole() == Role.LEAVE_MANAGER
				|| employeeRolesRequestDto.leaveRole == Role.LEAVE_ADMIN;

		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST.getKey(),
				isLeaveRequestNotificationsEnabled);
		notificationsObjectNode.put(NotificationSettingsType.TIME_ENTRY.getKey(), isTimeEntryNotificationsEnabled);
		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST_NUDGE.getKey(), isNudgeNotificationsEnabled);

		userSettings.setNotifications(notificationsObjectNode);
		userSettings.setUser(user);

		log.info("createNotificationSettings: execution ended");
		return userSettings;
	}

	private void saveEmployeeRoles(Employee employee) {
		log.info("saveEmployeeRoles: execution started");

		EmployeeRole superAdminRoles = new EmployeeRole();
		superAdminRoles.setEmployee(employee);
		superAdminRoles.setPeopleRole(Role.PEOPLE_EMPLOYEE);
		superAdminRoles.setLeaveRole(Role.LEAVE_EMPLOYEE);
		superAdminRoles.setAttendanceRole(Role.ATTENDANCE_EMPLOYEE);
		superAdminRoles.setIsSuperAdmin(false);
		superAdminRoles.setChangedDate(DateTimeUtils.getCurrentUtcDate());
		superAdminRoles.setRoleChangedBy(employee);

		employeeRoleDao.save(superAdminRoles);
		employee.setEmployeeRole(superAdminRoles);

		log.info("saveEmployeeRoles: execution started");
	}

	public void setBulkManagers(EmployeeBulkDto employeeBulkDto, EmployeeDetailsDto employeeDetailsDto) {
		if (employeeBulkDto.getPrimaryManager() != null) {
			Optional<User> byEmail = userDao.findByEmail(employeeBulkDto.getPrimaryManager());
			if (byEmail.isPresent()) {
				Optional<Employee> managerPrimary = employeeDao.findById(byEmail.get().getUserId());
				managerPrimary.ifPresent(value -> employeeDetailsDto.setPrimaryManager(value.getEmployeeId()));
			}
		}

		if (employeeBulkDto.getSecondaryManager() != null) {
			Optional<User> byEmail = userDao.findByEmail(employeeBulkDto.getSecondaryManager());
			if (byEmail.isPresent()) {
				Optional<Employee> secondaryManager = employeeDao.findById(byEmail.get().getUserId());
				secondaryManager.ifPresent(value -> employeeDetailsDto.setSecondaryManager(value.getEmployeeId()));
			}
		}
	}

	public void setBulkEmployeeProgression(EmployeeBulkDto employeeBulkDto, Employee employee) {
		if (employeeBulkDto.getEmployeeProgression() != null) {
			EmployeeProgression employeeProgression = peopleMapper
				.employeeProgressionDtoToEmployeeProgression(employeeBulkDto.getEmployeeProgression());
			if (employeeBulkDto.getEmployeeProgression().getEmployeeType() != null) {
				employee.setEmployeeType(employeeBulkDto.getEmployeeProgression().getEmployeeType());
			}

			if (employeeBulkDto.getEmployeeProgression().getJobFamilyId() != null) {
				employeeProgression.setJobFamilyId(employeeBulkDto.getEmployeeProgression().getJobFamilyId());
			}

			if (employeeBulkDto.getEmployeeProgression().getJobTitleId() != null) {
				employeeProgression.setJobTitleId(employeeBulkDto.getEmployeeProgression().getJobTitleId());
			}

			employeeProgression.setEmployee(employee);

			if (employeeBulkDto.getEmployeeProgression().getJobTitleId() != null
					&& employeeBulkDto.getEmployeeProgression().getJobFamilyId() != null)
				employee.setEmployeeProgressions(List.of(employeeProgression));
		}
	}

	public List<EmployeeDetailedResponseDto> fetchEmployeeSearchData(Page<Employee> employees) {
		List<EmployeeDetailedResponseDto> responseDtos = new ArrayList<>();
		for (Employee employee : employees.getContent()) {

			EmployeeDetailedResponseDto responseDto = peopleMapper.employeeToEmployeeDetailedResponseDto(employee);
			responseDto.setJobFamily(peopleMapper.jobFamilyToEmployeeJobFamilyDto(employee.getJobFamily()));
			Optional<EmployeePeriod> period = employeePeriodDao
				.findEmployeePeriodByEmployee_EmployeeIdAndIsActiveTrue(employee.getEmployeeId());
			period.ifPresent(employeePeriod -> responseDto
				.setPeriodResponseDto(peopleMapper.employeePeriodToEmployeePeriodResponseDto(employeePeriod)));
			responseDtos.add(responseDto);
		}
		return responseDtos;
	}

	private List<String> validateEmployeeBulkDto(EmployeeBulkDto employeeBulkDto) {
		List<String> errors = new ArrayList<>();

		validateMandatoryFields(employeeBulkDto);

		if (employeeBulkDto.getTimeZone() != null && !employeeBulkDto.getTimeZone().isBlank()
				&& !DateTimeUtils.isValidTimeZone(employeeBulkDto.getTimeZone())) {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_TIMEZONE);
		}

		if (employeeBulkDto.getIdentificationNo() != null)
			validateIdentificationNo(employeeBulkDto.getIdentificationNo(), errors);

		validateFirstName(employeeBulkDto.getFirstName(), errors);
		validateLastName(employeeBulkDto.getLastName(), errors);
		validateUserEmail(employeeBulkDto.getWorkEmail(), errors);
		validateUserSupervisor(employeeBulkDto.getPrimaryManager(), errors);
		validateUserSupervisor(employeeBulkDto.getSecondaryManager(), errors);
		validateCareerProgressionInBulk(employeeBulkDto.getEmployeeProgression(), errors);
		validateStateInBulk(employeeBulkDto.getEmployeePersonalInfo().getState(), errors);

		if (employeeBulkDto.getEmployeeEmergency() != null) {
			validateEmergencyContactName(employeeBulkDto.getEmployeeEmergency().getName(), errors);
			validatePhoneNumberInBulk(employeeBulkDto.getEmployeeEmergency().getContactNo(), errors);
		}
		if (employeeBulkDto.getPhone() != null)
			validatePhoneNumberInBulk(employeeBulkDto.getPhone(), errors);
		if (employeeBulkDto.getEmployeeEmergency() != null
				&& employeeBulkDto.getEmployeeEmergency().getContactNo() != null)
			validateEmergencyContactNumberInBulk(employeeBulkDto.getEmployeeEmergency().getContactNo(), errors);
		if (employeeBulkDto.getEmployeePersonalInfo().getNin() != null)
			validateNIN(employeeBulkDto.getEmployeePersonalInfo().getNin(), errors);
		if (employeeBulkDto.getAddress() != null)
			validateAddressInBulk(employeeBulkDto.getAddress(), errors);
		if (employeeBulkDto.getAddressLine2() != null)
			validateAddressInBulk(employeeBulkDto.getAddressLine2(), errors);
		validateStateInBulk(employeeBulkDto.getEmployeePersonalInfo().getCity(), errors);
		validatePassportNumber(employeeBulkDto.getEmployeePersonalInfo().getPassportNo(), errors);
		if (employeeBulkDto.getEmployeePersonalInfo().getSsn() != null) {
			validateSocialSecurityNumber(employeeBulkDto.getEmployeePersonalInfo().getSsn(), errors);
		}

		return errors;
	}

	public void validateNIN(String nin, List<String> errors) {
		if (!nin.trim().matches(VALID_NIN_NUMBER_REGEXP))
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_NIN));

		if (nin.length() > PeopleConstants.MAX_NIN_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_NIN_LENGTH,
					new Object[] { PeopleConstants.MAX_NIN_LENGTH }));
	}

	public void validatePassportNumber(String passportNumber, List<String> errors) {
		if (passportNumber != null && (!passportNumber.trim().matches(ALPHANUMERIC_REGEX))) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_PASSPORT));
		}

		if (passportNumber != null && passportNumber.length() > PeopleConstants.MAX_NIN_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_PASSPORT_LENGTH,
					new Object[] { PeopleConstants.MAX_NIN_LENGTH }));
	}

	public void validateIdentificationNo(String identificationNo, List<String> errors) {
		if (!Validations.isValidIdentificationNo(identificationNo)) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_IDENTIFICATION_NUMBER));
		}

		if (identificationNo.length() > PeopleConstants.MAX_ID_LENGTH)
			errors
				.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_IDENTIFICATION_NUMBER_LENGTH,
						new Object[] { PeopleConstants.MAX_ID_LENGTH }));
	}

	public void validateSocialSecurityNumber(String socialSecurityNumber, List<String> errors) {
		if (socialSecurityNumber != null && (!socialSecurityNumber.trim().matches(ALPHANUMERIC_REGEX))) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_SSN));
		}

		if (socialSecurityNumber != null && socialSecurityNumber.length() > PeopleConstants.MAX_SSN_LENGTH)
			errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_EXCEEDING_MAX_CHARACTER_LIMIT,
					new Object[] { PeopleConstants.MAX_SSN_LENGTH, "First Name" }));
	}

	public void validateAddressInBulk(String addressLine, List<String> errors) {
		if (!addressLine.trim().matches(ADDRESS_REGEX))
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_ADDRESS));

		if (addressLine.length() > PeopleConstants.MAX_ADDRESS_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_ADDRESS_LENGTH,
					new Object[] { PeopleConstants.MAX_ADDRESS_LENGTH }));

	}

	public void validateStateInBulk(String state, List<String> errors) {
		if (state != null && (!state.trim().matches(SPECIAL_CHAR_REGEX))) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_CITY_STATE));
		}

		if (state != null && state.length() > PeopleConstants.MAX_ADDRESS_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_STATE_PROVINCE,
					new Object[] { PeopleConstants.MAX_ADDRESS_LENGTH }));
	}

	public void validateFirstName(String firstName, List<String> errors) {
		if (firstName != null && (!firstName.trim().matches(NAME_REGEX))) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_FIRST_NAME));
		}

		if (firstName != null && firstName.length() > PeopleConstants.MAX_NAME_LENGTH)
			errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_EXCEEDING_MAX_CHARACTER_LIMIT,
					new Object[] { PeopleConstants.MAX_NAME_LENGTH, "First Name" }));

	}

	public void validateLastName(String lastName, List<String> errors) {
		if (lastName != null && (!lastName.trim().matches(NAME_REGEX))) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_LAST_NAME));
		}

		if (lastName != null && lastName.length() > PeopleConstants.MAX_NAME_LENGTH)
			errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_EXCEEDING_MAX_CHARACTER_LIMIT,
					new Object[] { PeopleConstants.MAX_NAME_LENGTH, "Last Name" }));
	}

	public void validateEmergencyContactName(String name, List<String> errors) {
		if (name != null && (!name.trim().matches(NAME_REGEX))) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMERGENCY_CONTACT_NAME));
		}

		if (name != null && name.length() > PeopleConstants.MAX_NAME_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_NAME_LENGTH,
					new Object[] { PeopleConstants.MAX_NAME_LENGTH }));
	}

	public void validatePhoneNumberInBulk(String phone, List<String> errors) {
		if (phone != null && !Validations.isValidPhoneNumber(phone)) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_PHONE_NUMBER));
		}

		if (phone != null && phone.length() > PeopleConstants.MAX_PHONE_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_PHONE_NUMBER_LENGTH,
					new Object[] { PeopleConstants.MAX_PHONE_LENGTH }));
	}

	public void validateEmergencyContactNumberInBulk(String phone, List<String> errors) {
		if (!Validations.isValidPhoneNumber(phone)) {
			errors.add(messageUtil
				.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMERGENCY_CONTACT_PHONE_NUMBER));
		}
	}

	private void validateMandatoryFields(EmployeeBulkDto employeeBulkDto) {
		List<String> missedFields = new ArrayList<>();

		if (employeeBulkDto.getFirstName() == null) {
			missedFields.add("First name");
		}
		if (employeeBulkDto.getLastName() == null) {
			missedFields.add("Last name");
		}

		if (employeeBulkDto.getWorkEmail() == null) {
			missedFields.add("Work Email");
		}

		if (!missedFields.isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_MISSING_USER_BULK_MANDATORY_FIELDS,
					missedFields);
		}
	}

	public List<EmployeeDataExportResponseDto> exportEmployeeData(Page<Employee> employees,
			List<EmployeeTeamDto> teamList, List<Long> employeeIds) {
		List<EmployeeManagerDto> employeeManagerDtos = employeeDao.findManagersByEmployeeIds(employeeIds);
		List<EmployeeDataExportResponseDto> responseDtos = new ArrayList<>();
		for (Employee employee : employees.getContent()) {
			EmployeeDataExportResponseDto responseDto = peopleMapper.employeeToEmployeeDataExportResponseDto(employee);
			responseDto.setJobFamily(peopleMapper.jobFamilyToJobFamilyDto(employee.getJobFamily()));
			responseDto.setJobTitle(peopleMapper.jobTitleToJobTitleDto(employee.getJobTitle()));

			List<Team> teams = teamList.stream()
				.filter(e -> Objects.equals(e.getEmployeeId(), employee.getEmployeeId()))
				.map(EmployeeTeamDto::getTeam)
				.toList();

			responseDto.setTeamResponseDto(peopleMapper.teamListToTeamResponseDtoList(teams));

			List<Employee> managers = employeeManagerDtos.stream()
				.filter(e -> Objects.equals(e.getEmployeeId(), employee.getEmployeeId()))
				.map(EmployeeManagerDto::getManagers)
				.toList();
			responseDto.setManagers(peopleMapper.employeeListToEmployeeResponseDtoList(managers));
			Optional<EmployeePeriod> period = employeePeriodDao
				.findEmployeePeriodByEmployee_EmployeeIdAndIsActiveTrue(employee.getEmployeeId());
			period.ifPresent(employeePeriod -> responseDto
				.setEmployeePeriod(peopleMapper.employeePeriodToEmployeePeriodResponseDto(employeePeriod)));
			responseDtos.add(responseDto);
		}
		return responseDtos;
	}

	private TransactionTemplate getTransactionManagerTemplate() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(Propagation.REQUIRED.value());
		transactionTemplate.setIsolationLevel(Isolation.DEFAULT.value());
		return transactionTemplate;
	}

	private void validateUserEmail(String workEmail, List<String> errors) {
		if (workEmail != null && (workEmail.matches(Validation.EMAIL_REGEX))) {
			Optional<User> userBulkDtoUser = userDao.findByEmail(workEmail);
			if (userBulkDtoUser.isPresent()) {
				errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_USER_EMAIL_ALREADY_EXIST));
			}
		}
		else {
			errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_INVALID_EMAIL));
		}

		if (workEmail != null && workEmail.length() > PeopleConstants.MAX_EMAIL_LENGTH)
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMAIL_LENGTH,
					new Object[] { PeopleConstants.MAX_EMAIL_LENGTH }));
	}

	private void validateUserSupervisor(String supervisorEmail, List<String> errors) {
		if (supervisorEmail != null) {
			Optional<User> managerUser = userDao.findByEmail(supervisorEmail);
			if (managerUser.isEmpty()) {
				errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_SUPERVISOR_NOT_FOUND));
			}
			else {
				if (Boolean.FALSE.equals(managerUser.get().getEmployee().getUser().getIsActive()))
					errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_SUPERVISOR_NOT_FOUND));
				else {
					Optional<Employee> primaryManagerEmployee = employeeDao.findById(managerUser.get().getUserId());
					if (primaryManagerEmployee.isEmpty()) {
						errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_SUPERVISOR_NOT_FOUND));
					}
				}
			}
		}
	}

	private void validateCareerProgressionInBulk(EmployeeProgressionsDto employeeProgressionsDto, List<String> errors) {
		if (employeeProgressionsDto != null) {
			if (employeeProgressionsDto.getJobFamilyId() != null) {
				Optional<JobFamily> jobRole = jobFamilyDao
					.findByJobFamilyIdAndIsActive(employeeProgressionsDto.getJobFamilyId(), true);
				if (jobRole.isEmpty()) {
					errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_JOB_FAMILY_NOT_FOUND));
				}
			}
			if (employeeProgressionsDto.getJobTitleId() != null) {
				Optional<JobTitle> jobLevel = jobTitleDao
					.findByJobTitleIdAndIsActive(employeeProgressionsDto.getJobTitleId(), true);
				if (jobLevel.isEmpty()) {
					errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_JOB_TITLE_NOT_FOUND));
				}
			}
			if (employeeProgressionsDto.getStartDate() != null && employeeProgressionsDto.getEndDate() != null
					&& DateTimeUtils.isValidDateRange(employeeProgressionsDto.getStartDate(),
							employeeProgressionsDto.getEndDate())) {
				errors.add(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_INVALID_START_END_DATE));
			}
		}
	}

	private void validatePhoneNumber(String phoneNumber) {
		if (phoneNumber != null && !phoneNumber.isEmpty() && !Validations.isValidPhoneNumber(phoneNumber)) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_PHONE_NUMBER);
		}
	}

	private void processEmploymentDetails(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (finalEmployee.getIdentificationNo() != null)
			finalEmployee.setIdentificationNo(finalEmployee.getIdentificationNo().toUpperCase());

		if (employeeDetailsDto.getEeo() != null)
			finalEmployee.setEeo(employeeDetailsDto.getEeo());

		if (employeeDetailsDto.getEmploymentAllocation() != null)
			finalEmployee.setEmploymentAllocation(employeeDetailsDto.getEmploymentAllocation());

		Set<Long> teamIds = employeeDetailsDto.getTeams();
		if (teamIds != null && !teamIds.isEmpty()) {
			Set<EmployeeTeam> employeeTeams = getEmployeeTeams(teamIds, finalEmployee);
			finalEmployee.setTeams(employeeTeams);
		}
	}

	private void processAndUpdateEmployeeDetails(EmployeeUpdateDto updateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines) {
		if (updateDto.getIdentificationNo() != null) {
			updateDto.setIdentificationNo(updateDto.getIdentificationNo().toUpperCase());

			if (Validations.isValidIdentificationNo(updateDto.getIdentificationNo())) {
				employee.setIdentificationNo(updateDto.getIdentificationNo());
			}
			else {
				throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_IDENTIFICATION_NUMBER);
			}
		}
		validateAndUpdateEmployeeGeneralInfo(updateDto, employee);
		if (updateDto.getEmployeeEmergency() != null && !updateDto.getEmployeeEmergency().isEmpty()) {
			updateEmployeeEmergencies(updateDto, employee);
		}

		if (updateDto.getEmployeePersonalInfo() != null) {
			updatePersonInfo(updateDto, employee);
		}

		if (updateDto.getEmployeeVisas() != null) {
			updateEmployeeVisas(updateDto, employee);
		}

		if (updateDto.getEmployeeEducations() != null) {
			updateEmployeeEducations(updateDto, employee);
		}

		if (updateDto.getEmployeeProgressions() != null) {
			updateEmployeeProgression(updateDto, employee, employeeTimelines);
		}

		if (updateDto.getTeams() != null) {
			updateEmployeeTeam(updateDto.getTeams(), employee);
		}

		if (updateDto.getEmployeeFamilies() != null) {
			updateEmployeeFamilies(updateDto, employee);
		}
		validateAndUpdateEmploymentData(updateDto, employee, employeeTimelines);

	}

	private void validateAndUpdateEmploymentData(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeUpdateDto.getEmploymentAllocation() != null) {
			employee.setEmploymentAllocation(employeeUpdateDto.getEmploymentAllocation());
		}
		if (employeeUpdateDto.getAccountStatus() != null) {
			employee.setAccountStatus(employeeUpdateDto.getAccountStatus());
			if (employeeUpdateDto.getAccountStatus() == AccountStatus.TERMINATED) {
				employee.getUser().setIsActive(false);
			}
			else if (employeeUpdateDto.getAccountStatus() == AccountStatus.ACTIVE) {
				employee.getUser().setIsActive(true);
			}
		}

		if (employeeUpdateDto.getJoinDate() != null) {
			List<EmployeeTimeline> joinedDateHistoryRecords = employeeTimelineDao
				.findByEmployeeAndTimelineType(employee, EmployeeTimelineType.JOINED_DATE);
			employeeTimelineDao.deleteAllById(joinedDateHistoryRecords.stream().map(EmployeeTimeline::getId).toList());
			employee.setJoinDate(employeeUpdateDto.getJoinDate());
			employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.JOINED_DATE,
					EmployeeTimelineConstant.TITLE_JOINED_DATE, null, String.valueOf(employeeUpdateDto.getJoinDate())));
		}

		updateEmployeePeriod(employeeUpdateDto, employee, employeeTimelines);
		updateEmploymentTypeTimeline(employee, employeeUpdateDto, employeeTimelines);
		updateEmploymentAllocationTimeline(employee, employeeUpdateDto, employeeTimelines);
		updateJobProgressionTimeline(employee, employeeUpdateDto, employeeTimelines);
	}

	private void updateEmployeePeriod(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeUpdateDto.getEmployeePeriod() == null) {
			return;
		}

		Optional<EmployeePeriod> employeePeriodOpt = employeePeriodDao
			.findEmployeePeriodByEmployee_EmployeeId(employee.getEmployeeId());

		if (employeePeriodOpt.isPresent()) {
			EmployeePeriod employeePeriod = employeePeriodOpt.get();
			updateExistingEmployeePeriod(employeeUpdateDto, employee, employeeTimelines, employeePeriod);
		}
		else {
			saveEmployeePeriod(employee, employeeUpdateDto.getEmployeePeriod());
		}
	}

	private void updateExistingEmployeePeriod(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines, EmployeePeriod employeePeriod) {
		updateStartDate(employeeUpdateDto, employee, employeeTimelines, employeePeriod);
		updateEndDate(employeeUpdateDto, employee, employeeTimelines, employeePeriod);
		employeePeriod.setActive(false);
	}

	private void updateStartDate(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines, EmployeePeriod employeePeriod) {
		LocalDate newStartDate = employeeUpdateDto.getEmployeePeriod().getStartDate();

		employeePeriod.setStartDate(newStartDate);

		if (newStartDate == null) {
			newStartDate = LocalDate.now();
			addTimelineEntry(employee, employeeTimelines, EmployeeTimelineType.PROBATION_START_DATE,
					EmployeeTimelineConstant.TITLE_PROBATION_START_DATE_ADDED, null, newStartDate.toString());
		}
		else if (employeePeriod.getStartDate() != null && !employeePeriod.getStartDate().equals(newStartDate)) {
			addTimelineEntry(employee, employeeTimelines, EmployeeTimelineType.PROBATION_START_DATE,
					EmployeeTimelineConstant.TITLE_PROBATION_START_DATE_CHANGED,
					employeePeriod.getStartDate().toString(), newStartDate.toString());
		}
	}

	private void updateEndDate(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines, EmployeePeriod employeePeriod) {
		LocalDate newEndDate = employeeUpdateDto.getEmployeePeriod().getEndDate();

		employeePeriod.setEndDate(newEndDate);

		if (newEndDate == null) {
			newEndDate = LocalDate.now();
			addTimelineEntry(employee, employeeTimelines, EmployeeTimelineType.PROBATION_START_DATE,
					EmployeeTimelineConstant.TITLE_PROBATION_START_DATE_ADDED, null, newEndDate.toString());
		}

		if (employeePeriod.getEndDate() != null && !employeePeriod.getEndDate().equals(newEndDate)) {
			addTimelineEntry(employee, employeeTimelines, EmployeeTimelineType.PROBATION_END_DATE,
					EmployeeTimelineConstant.TITLE_PROBATION_END_DATE_CHANGED, employeePeriod.getEndDate().toString(),
					newEndDate.toString());
		}
		else if (employeePeriod.getEndDate() == null) {
			addTimelineEntry(employee, employeeTimelines, EmployeeTimelineType.PROBATION_END_DATE,
					EmployeeTimelineConstant.TITLE_PROBATION_END_DATE_ADDED, null, newEndDate.toString());
		}
	}

	private void updateEmploymentAllocationTimeline(Employee employee, EmployeeUpdateDto employeeUpdateDto,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeUpdateDto.getEmploymentAllocation() != null) {
			EmployeeTimeline employmentAllocationRecord = getEmployeeTimeline(employee,
					EmployeeTimelineType.EMPLOYMENT_ALLOCATION_CHANGED, "Title Employment Allocation Changed", null,
					employeeUpdateDto.getEmploymentAllocation().toString());

			employeeTimelines.add(employmentAllocationRecord);
		}
	}

	private void updateEmploymentTypeTimeline(Employee employee, EmployeeUpdateDto employeeUpdateDto,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeUpdateDto.getEmployeeProgressions() != null
				&& !employeeUpdateDto.getEmployeeProgressions().isEmpty()
				&& employeeUpdateDto.getEmployeeProgressions().getFirst().getEmployeeType() != null) {

			EmployeeType currentEmploymentType = employeeUpdateDto.getEmployeeProgressions()
				.getFirst()
				.getEmployeeType();

			EmployeeTimeline employmentTypeRecord = getEmployeeTimeline(employee,
					EmployeeTimelineType.EMPLOYMENT_TYPE_CHANGED, TITLE_EMPLOYMENT_TYPE_CHANGED, null,
					currentEmploymentType.toString());

			employeeTimelines.add(employmentTypeRecord);
		}
	}

	private void updateJobProgressionTimeline(Employee employee, EmployeeUpdateDto employeeUpdateDto,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeUpdateDto.getEmployeeProgressions() != null
				&& !employeeUpdateDto.getEmployeeProgressions().isEmpty()) {
			EmployeeProgressionsDto currentProgression = employeeUpdateDto.getEmployeeProgressions().getFirst();

			if (currentProgression.getJobTitleId() != null) {
				Optional<JobTitle> jobTitle = jobTitleDao.findById(currentProgression.getJobTitleId());
				if (jobTitle.isPresent()) {
					String newTitle = jobTitle.get().getName();
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.JOB_TITLE_CHANGED,
							TITLE_JOB_TITLE_CHANGED, null, newTitle));
				}
			}

			if (currentProgression.getJobFamilyId() != null) {
				Optional<JobFamily> jobFamily = jobFamilyDao.findById(currentProgression.getJobFamilyId());
				if (jobFamily.isPresent()) {
					String newFamily = jobFamily.get().getName();
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.JOB_FAMILY_CHANGED,
							TITLE_JOB_FAMILY_CHANGED, null, newFamily));
				}
			}
		}
	}

	private void addTimelineEntry(Employee employee, List<EmployeeTimeline> employeeTimelines,
			EmployeeTimelineType timelineType, String title, String previousValue, String newValue) {
		employeeTimelines.add(getEmployeeTimeline(employee, timelineType, title, previousValue, newValue));
	}

	private void processEmployeeProgressions(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (employeeDetailsDto.getEmployeeProgressions() != null
				&& !employeeDetailsDto.getEmployeeProgressions().isEmpty()) {
			saveCareerProgression(finalEmployee, employeeDetailsDto.getEmployeeProgressions());
		}
	}

	private void processEmployeeEmergencyContacts(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (employeeDetailsDto.getEmployeeEmergency() != null && !employeeDetailsDto.getEmployeeEmergency().isEmpty()) {
			for (EmployeeEmergencyDto emergencyContact : employeeDetailsDto.getEmployeeEmergency()) {
				if (emergencyContact != null) {
					String contactNo = emergencyContact.getContactNo();
					if (Validations.isValidPhoneNumber(contactNo)) {
						List<EmployeeEmergency> employeeEmergency = peopleMapper
							.employeeEmergencyDtoListToEmployeeEmergencyList(employeeDetailsDto.getEmployeeEmergency());
						finalEmployee.setEmployeeEmergencies(employeeEmergency);
						employeeEmergency.forEach(em -> em.setEmployee(finalEmployee));
					}
					else {
						throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_PHONE_NUMBER);
					}
				}
			}
		}
	}

	private void processEmployeeVisas(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (employeeDetailsDto.getEmployeeVisas() != null && !employeeDetailsDto.getEmployeeVisas().isEmpty()) {
			setEmploymentVisa(finalEmployee, employeeDetailsDto.getEmployeeVisas());
		}
	}

	private void processEmployeeFamilies(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (employeeDetailsDto.getEmployeeFamilies() != null && !employeeDetailsDto.getEmployeeFamilies().isEmpty()) {
			setEmployeeFamilies(finalEmployee, employeeDetailsDto.getEmployeeFamilies());
		}
	}

	private void processEmployeeEducations(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (employeeDetailsDto.getEmployeeEducations() != null
				&& !employeeDetailsDto.getEmployeeEducations().isEmpty()) {
			setEmployeeEducations(finalEmployee, employeeDetailsDto.getEmployeeEducations());
		}
	}

	private void processEmployeePersonalInfo(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		if (employeeDetailsDto.getEmployeePersonalInfo() != null) {
			JsonNode previousEmploymentDetails = employeeDetailsDto.getEmployeePersonalInfo()
				.getPreviousEmploymentDetails();
			if (previousEmploymentDetails != null && !previousEmploymentDetails.isEmpty()
					&& previousEmploymentDetails.isArray()) {
				validateEmploymentDates(previousEmploymentDetails);
			}
			EmployeePersonalInfo employeePersonalInfo = peopleMapper
				.employeePersonalInfoDtoToEmployeePersonalInfo(employeeDetailsDto.getEmployeePersonalInfo());
			employeePersonalInfo.setEmployee(finalEmployee);
			finalEmployee.setPersonalInfo(employeePersonalInfo);
		}
	}

	private void validateAndUpdateEmployeeGeneralInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getAuthPic() != null) {
			employee.setAuthPic(employeeUpdateDto.getAuthPic());
		}
		if (employeeUpdateDto.getAddress() != null) {
			employee.setAddress(employeeUpdateDto.getAddress());
		}
		if (employeeUpdateDto.getAddressLine2() != null) {
			employee.setAddressLine2(employeeUpdateDto.getAddressLine2());
		}
		if (employeeUpdateDto.getPersonalEmail() != null) {
			employee.setPersonalEmail(employeeUpdateDto.getPersonalEmail());
		}
		if (employeeUpdateDto.getGender() != null) {
			employee.setGender(employeeUpdateDto.getGender());
		}
		validateAndUpdateEmployeeName(employeeUpdateDto, employee);
		validatePhoneNumber(employeeUpdateDto.getPhone());
		if (employeeUpdateDto.getCountry() != null) {
			employee.setCountry(employeeUpdateDto.getCountry());
		}
		if (employeeUpdateDto.getTimeZone() != null) {
			employee.setTimeZone(employeeUpdateDto.getTimeZone());
		}
		if (employeeUpdateDto.getEeo() != null) {
			employee.setEeo(employeeUpdateDto.getEeo());
		}
		if (employeeUpdateDto.getPhone() != null) {
			employee.setPhone(employeeUpdateDto.getPhone());
		}
	}

	private void validateAndUpdateEmployeeName(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getFirstName() != null
				&& Validations.isEmployeeNameValid(employeeUpdateDto.getFirstName())) {
			employee.setFirstName(employeeUpdateDto.getFirstName());
		}
		if (employeeUpdateDto.getMiddleName() != null && !employeeUpdateDto.getMiddleName().isBlank()
				&& Validations.isEmployeeNameValid(employeeUpdateDto.getMiddleName())) {
			employee.setMiddleName(employeeUpdateDto.getMiddleName());
		}
		else if (employeeUpdateDto.getMiddleName() != null && employeeUpdateDto.getMiddleName().isBlank()) {
			employee.setMiddleName(null);
		}
		if (employeeUpdateDto.getLastName() != null
				&& Validations.isEmployeeNameValid(employeeUpdateDto.getLastName())) {
			employee.setLastName(employeeUpdateDto.getLastName());
		}
	}

	private void validateCareerProgressionData(List<EmployeeProgressionsDto> employeeProgressionsDtos) {
		employeeProgressionsDtos.forEach(employeeProgressionsDto -> {
			if (employeeProgressionsDto.getJobFamilyId() != null) {
				boolean jobFamilyExists = jobFamilyDao
					.existsByJobFamilyIdAndIsActive(employeeProgressionsDto.getJobFamilyId(), true);
				if (!jobFamilyExists) {
					throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_JOB_FAMILY_NOT_FOUND);
				}
			}

			if (employeeProgressionsDto.getJobTitleId() != null) {
				boolean jobTitleExists = jobTitleDao
					.existsByJobTitleIdAndIsActive(employeeProgressionsDto.getJobTitleId(), true);
				if (!jobTitleExists) {
					throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_JOB_TITLE_NOT_FOUND);
				}
			}

			if (Validation.isInvalidStartAndEndDate(employeeProgressionsDto.getStartDate(),
					employeeProgressionsDto.getEndDate())) {
				throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_START_END_DATE);
			}
		});
	}

	private Set<EmployeeTeam> getEmployeeTeams(Set<Long> teamIds, Employee finalEmployee) {
		List<Team> teams = teamDao.findAllById(teamIds);

		if (teamIds.size() != teams.size()) {
			log.info("getEmployeeTeams: Team ID(s) are not valid");
		}

		Set<EmployeeTeam> employeeTeams = new HashSet<>();
		if (!teams.isEmpty()) {
			for (Team team : teams) {
				EmployeeTeam employeeTeam = new EmployeeTeam();
				employeeTeam.setTeam(team);
				employeeTeam.setEmployee(finalEmployee);
				employeeTeam.setIsSupervisor(false);
				employeeTeams.add(employeeTeam);
				employeeTeam.setIsSupervisor(false);
			}
		}
		else {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_TEAM_NOT_FOUND);
		}
		return employeeTeams;
	}

	private Set<EmployeeManager> addNewManagers(EmployeeDetailsDto employeeDetailsDto, Employee finalEmployee) {
		Set<EmployeeManager> employeeManagers = new HashSet<>();

		if (employeeDetailsDto.getPrimaryManager() != null) {
			Employee manager = getManager(employeeDetailsDto.getPrimaryManager());

			if (manager != null) {
				addManagersToEmployee(manager, finalEmployee, employeeManagers, true);
			}

			if (employeeDetailsDto.getSecondaryManager() != null) {
				Employee secondaryManager = getManager(employeeDetailsDto.getSecondaryManager());
				if (manager != null && manager.equals(secondaryManager)) {
					throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_SECONDARY_MANAGER_DUPLICATE);
				}
				addManagersToEmployee(secondaryManager, finalEmployee, employeeManagers, false);
			}
		}

		return employeeManagers;
	}

	private void addManagersToEmployee(Employee manager, Employee finalEmployee, Set<EmployeeManager> employeeManagers,
			boolean directManager) {

		EmployeeManager employeeManager = createEmployeeManager(manager, finalEmployee, directManager);
		employeeManagers.add(employeeManager);

		addTimelineRecord(finalEmployee, employeeManager, manager);
	}

	private Employee getManager(Long managerId) {
		return employeeDao.findEmployeeByEmployeeIdAndUserActiveNot(managerId, false)
			.orElseThrow(() -> new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_MANAGER_NOT_FOUND));
	}

	private EmployeeManager createEmployeeManager(Employee manager, Employee employee, boolean directManager) {
		EmployeeManager employeeManager = new EmployeeManager();
		employeeManager.setManager(manager);
		employeeManager.setEmployee(employee);
		employeeManager.setPrimaryManager(directManager);
		employeeManager.setManagerType(directManager ? ManagerType.PRIMARY : ManagerType.SECONDARY);
		return employeeManager;
	}

	private void addTimelineRecord(Employee employee, EmployeeManager employeeManager, Employee manager) {

		String managerTypeTitle;
		if (employeeManager.isPrimaryManager()) {
			managerTypeTitle = EmployeeTimelineConstant.TITLE_PRIMARY_MANAGER_CHANGED;
		}
		else {
			managerTypeTitle = EmployeeTimelineConstant.TITLE_SECONDARY_MANAGER_ASSIGNED;
		}

		employeeTimelineService.addEmployeeTimelineRecord(employee, EmployeeTimelineType.MANAGER_ASSIGNED,
				managerTypeTitle, null, manager.getFirstName() + " " + manager.getLastName());
	}

	private void saveCareerProgression(Employee finalEmployee, List<EmployeeProgressionsDto> employeeProgressions) {
		List<EmployeeProgression> employeeProgressionList = new ArrayList<>();
		List<Long> updatingIdList = new ArrayList<>();

		employeeProgressions.forEach(employeeProgressionsDto -> {
			EmployeeProgression employeeProgression;
			Optional<EmployeeProgression> employeeProgressionOpt = Optional.empty();
			if (finalEmployee.getEmployeeProgressions() != null)
				employeeProgressionOpt = finalEmployee.getEmployeeProgressions()
					.stream()
					.filter(progression -> employeeProgressionsDto.getProgressionId() != null
							&& progression.getProgressionId().equals(employeeProgressionsDto.getProgressionId()))
					.findFirst();

			if (employeeProgressionOpt.isPresent()) {
				employeeProgression = employeeProgressionOpt.get();
				employeeProgression.setIsCurrent(employeeProgressionsDto.getIsCurrent() != null
						? employeeProgressionsDto.getIsCurrent() : employeeProgression.getIsCurrent());
				employeeProgression.setEmployeeType(employeeProgressionsDto.getEmployeeType() != null
						? employeeProgressionsDto.getEmployeeType() : employeeProgression.getEmployeeType());
				employeeProgression.setStartDate(employeeProgressionsDto.getStartDate() != null
						? employeeProgressionsDto.getStartDate() : employeeProgression.getStartDate());
				employeeProgression.setEndDate(employeeProgressionsDto.getEndDate() != null
						? employeeProgressionsDto.getEndDate() : employeeProgression.getEndDate());
				updatingIdList.add(employeeProgression.getProgressionId());
			}
			else {
				employeeProgression = peopleMapper.employeeProgressionDtoToEmployeeProgression(employeeProgressionsDto);
				employeeProgression.setEmployee(finalEmployee);
			}

			if (employeeProgressionsDto.getJobFamilyId() != null) {
				employeeProgression.setJobFamilyId(employeeProgressionsDto.getJobFamilyId());

				if (Boolean.TRUE.equals(employeeProgressionsDto.getIsCurrent())) {
					finalEmployee.setEmployeeType(employeeProgressionsDto.getEmployeeType());
				}
			}

			if (employeeProgressionsDto.getJobTitleId() != null) {
				employeeProgression.setJobTitleId(employeeProgressionsDto.getJobTitleId());

				if (Boolean.TRUE.equals(employeeProgressionsDto.getIsCurrent())) {
					finalEmployee.setEmployeeType(employeeProgressionsDto.getEmployeeType());
				}
			}

			if (employeeProgressionOpt.isEmpty()) {
				employeeProgressionList.add(employeeProgression);
			}
		});

		if (finalEmployee.getEmployeeProgressions() != null && !finalEmployee.getEmployeeProgressions().isEmpty()) {
			if (finalEmployee.getEmployeeProgressions().size() <= updatingIdList.size()) {
				finalEmployee.getEmployeeProgressions().addAll(employeeProgressionList);
			}
			else {
				List<Long> currentIdList = finalEmployee.getEmployeeProgressions()
					.stream()
					.map(EmployeeProgression::getProgressionId)
					.toList();
				currentIdList.forEach(item -> {
					if (!updatingIdList.contains(item)) {
						Optional<EmployeeProgression> progressionOptional = employeeProgressionDao
							.findByProgressionId(item);
						if (progressionOptional.isPresent()) {
							employeeProgressionDao.deleteById(progressionOptional.get().getProgressionId());
							finalEmployee.getEmployeeProgressions().remove(progressionOptional.get());
						}
					}
				});

				finalEmployee.getEmployeeProgressions().addAll(employeeProgressionList);

			}
		}
		else {
			finalEmployee.setEmployeeProgressions(employeeProgressionList);
		}
	}

	private EmployeePeriod saveEmployeePeriod(Employee finalEmployee, ProbationPeriodDto probationPeriodDto) {
		EmployeePeriod employeePeriod = new EmployeePeriod();
		employeePeriod.setEmployee(finalEmployee);
		employeePeriod.setStartDate(probationPeriodDto.getStartDate());
		employeePeriod.setEndDate(probationPeriodDto.getEndDate());
		employeePeriod.setActive(false);
		return employeePeriodDao.save(employeePeriod);
	}

	private void setEmploymentVisa(Employee finalEmployee, List<EmploymentVisaDto> employmentVisas) {
		List<EmployeeVisa> employeeVisaFinal = peopleMapper.employeeVisaDtoListToEmployeeVisaList(employmentVisas);
		employeeVisaFinal.forEach(employeeVisa -> employeeVisa.setEmployee(finalEmployee));
		finalEmployee.setEmployeeVisas(employeeVisaFinal);
	}

	private void setEmployeeFamilies(Employee finalEmployee, List<EmployeeFamilyDto> employeeFamilies) {
		List<EmployeeFamily> employeeFamiliesFinal = peopleMapper
			.employeeFamilyDtoListToEmployeeFamilyList(employeeFamilies);
		employeeFamiliesFinal.forEach(employeeFamily -> employeeFamily.setEmployee(finalEmployee));
		finalEmployee.setEmployeeFamilies(employeeFamiliesFinal);
	}

	private void setEmployeeEducations(Employee finalEmployee, List<EmployeeEducationDto> employeeEducations) {
		List<EmployeeEducation> employeeEducationFinal = peopleMapper
			.employeeEducationDtoListToEmployeeEducationList(employeeEducations);
		employeeEducationFinal.forEach(employeeEdu -> employeeEdu.setEmployee(finalEmployee));
		finalEmployee.setEmployeeEducations(employeeEducationFinal);
	}

	public static void validateEmploymentDates(JsonNode previousEmploymentDetails) {
		LocalDate currentDate = DateTimeUtils.getCurrentUtcDate();
		for (JsonNode employmentDetail : previousEmploymentDetails) {
			if (!employmentDetail.has(START_DATE) || !employmentDetail.has(END_DATE)
					|| employmentDetail.get(START_DATE).isNull() || employmentDetail.get(START_DATE).asText().isEmpty()
					|| employmentDetail.get(END_DATE).isNull() || employmentDetail.get(END_DATE).asText().isEmpty()) {
				throw new ModuleException(
						PeopleMessageConstant.PEOPLE_ERROR_MISSING_PREVIOUS_EMPLOYMENT_START_AND_END_DATES);
			}

			LocalDate startDate = LocalDate.parse(employmentDetail.get(START_DATE).asText());
			LocalDate endDate = LocalDate.parse(employmentDetail.get(END_DATE).asText());
			if (startDate.isAfter(currentDate)) {
				throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_PREVIOUS_EMPLOYMENT_START_DATE_INVALID);
			}
			if (endDate.isAfter(currentDate)) {
				throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_PREVIOUS_EMPLOYMENT_END_DATE_INVALID);
			}
		}
	}

	private void updateEmployeeEmergencies(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		Optional<EmployeeEmergency> primaryEmergencyContact = findEmergencyContact(employee, true);
		Optional<EmployeeEmergency> secondaryEmergencyContact = findEmergencyContact(employee, false);

		for (EmployeeEmergencyDto dto : employeeUpdateDto.getEmployeeEmergency()) {
			if (dto.getEmergencyId() == null) {
				addNewEmergencyContact(employee, dto);
			}
			else if (Boolean.TRUE.equals(dto.getIsPrimary())) {
				updateEmergencyContact(primaryEmergencyContact, dto);
			}
			else {
				updateEmergencyContact(secondaryEmergencyContact, dto);
			}
		}
	}

	private Optional<EmployeeEmergency> findEmergencyContact(Employee employee, boolean isPrimary) {
		return employee.getEmployeeEmergencies()
			.stream()
			.filter(contact -> contact.getIsPrimary() == isPrimary)
			.findFirst();
	}

	private void updateEmergencyContact(Optional<EmployeeEmergency> emergencyContact, EmployeeEmergencyDto dto) {
		if (emergencyContact.isPresent()
				&& Objects.equals(emergencyContact.get().getEmergencyId(), dto.getEmergencyId())) {
			emergencyContact.get().setName(dto.getName());
			emergencyContact.get().setEmergencyRelationship(dto.getEmergencyRelationship());
			updateEmergencyContactNumber(emergencyContact.get(), dto.getContactNo());
		}
	}

	private void updateEmergencyContactNumber(EmployeeEmergency contact, String contactNo) {
		if (Validations.isValidPhoneNumber(contactNo)) {
			contact.setContactNo(contactNo);
		}
		else {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_INVALID_PHONE_NUMBER);
		}
	}

	private void addNewEmergencyContact(Employee employee, EmployeeEmergencyDto dto) {
		EmployeeEmergency newEmergency = new EmployeeEmergency();
		newEmergency.setName(dto.getName());
		newEmergency.setEmergencyRelationship(dto.getEmergencyRelationship());
		newEmergency.setIsPrimary(dto.getIsPrimary());
		updateEmergencyContactNumber(newEmergency, dto.getContactNo());
		newEmergency.setEmployee(employee);
		employee.getEmployeeEmergencies().add(newEmergency);
	}

	private void updateEmployeeVisas(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeVisas().isEmpty()) {
			clearEmployeeVisas(employee);
			return;
		}

		if (employee.getEmployeeVisas() == null || employee.getEmployeeVisas().isEmpty()) {
			setEmploymentVisa(employee, employeeUpdateDto.getEmployeeVisas());
			return;
		}

		Validations.validateVisaDates(employeeUpdateDto.getEmployeeVisas());
		List<Long> currentIdList = employee.getEmployeeVisas().stream().map(EmployeeVisa::getVisaId).toList();
		List<Long> updatingIdList = new ArrayList<>();

		updateExistingVisas(employeeUpdateDto, employee, updatingIdList);
		removeObsoleteVisas(employee, currentIdList, updatingIdList);
	}

	private void clearEmployeeVisas(Employee employee) {
		employee.getEmployeeVisas().forEach(employeeVisa -> employeeVisaDao.deleteById(employeeVisa.getVisaId()));
		employee.getEmployeeVisas().clear();
	}

	private void updateExistingVisas(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<Long> updatingIdList) {
		employeeUpdateDto.getEmployeeVisas().forEach(visa -> {
			Optional<EmployeeVisa> visaOpt = employee.getEmployeeVisas()
				.stream()
				.filter(vs -> visa.getVisaId() != null && vs.getVisaId().equals(visa.getVisaId()))
				.findFirst();
			if (visaOpt.isPresent()) {
				EmployeeVisa vs = updateVisaInfo(visa, visaOpt);
				updatingIdList.add(vs.getVisaId());
			}
			else {
				EmployeeVisa newEmployeeVisa = peopleMapper.employeeVisaDtoToEmployeeVisa(visa);
				newEmployeeVisa.setEmployee(employee);
				employee.getEmployeeVisas().add(newEmployeeVisa);
			}
		});
	}

	private void removeObsoleteVisas(Employee employee, List<Long> currentIdList, List<Long> updatingIdList) {
		currentIdList.forEach(item -> {
			if (!updatingIdList.contains(item)) {
				Optional<EmployeeVisa> visaOptional = employeeVisaDao.findByVisaId(item);
				visaOptional.ifPresent(visa -> {
					employeeVisaDao.deleteById(visa.getVisaId());
					employee.getEmployeeVisas().remove(visa);
				});
			}
		});
	}

	@NotNull
	private EmployeeVisa updateVisaInfo(EmploymentVisaDto visa, Optional<EmployeeVisa> visaOpt) {
		if (visaOpt.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_VISA_DETAILS_NOT_FOUND);
		}

		EmployeeVisa vs = visaOpt.get();
		vs.setVisaType(visa.getVisaType() != null ? visa.getVisaType() : vs.getVisaType());
		vs.setIssuedDate(visa.getIssuedDate() != null ? visa.getIssuedDate() : vs.getIssuedDate());
		vs.setExpirationDate(visa.getExpirationDate() != null ? visa.getExpirationDate() : vs.getExpirationDate());
		vs.setIssuingCountry(visa.getIssuingCountry() != null ? visa.getIssuingCountry() : vs.getIssuingCountry());
		return vs;
	}

	private void updateEmployeeEducations(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeEducations().isEmpty()) {
			clearEmployeeEducations(employee);
			return;
		}

		if (employee.getEmployeeEducations() == null || employee.getEmployeeEducations().isEmpty()) {
			setEmployeeEducations(employee, employeeUpdateDto.getEmployeeEducations());
			return;
		}

		List<Long> currentIdList = employee.getEmployeeEducations()
			.stream()
			.map(EmployeeEducation::getEducationId)
			.toList();
		List<Long> updatingIdList = new ArrayList<>();

		updateOrAddEducations(employeeUpdateDto, employee, updatingIdList);
		removeObsoleteEducations(employee, currentIdList, updatingIdList);
	}

	private void clearEmployeeEducations(Employee employee) {
		employee.getEmployeeEducations()
			.forEach(education -> employeeEducationDao.deleteById(education.getEducationId()));
		employee.getEmployeeEducations().clear();
	}

	private void updateOrAddEducations(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<Long> updatingIdList) {
		employeeUpdateDto.getEmployeeEducations().forEach(employeeEducationDto -> {
			Optional<EmployeeEducation> eduOpt = employee.getEmployeeEducations()
				.stream()
				.filter(edu -> employeeEducationDto.getEducationId() != null
						&& edu.getEducationId().equals(employeeEducationDto.getEducationId()))
				.findFirst();
			if (eduOpt.isPresent()) {
				EmployeeEducation ed = updateEducationInfo(employeeEducationDto, eduOpt);
				updatingIdList.add(ed.getEducationId());
			}
			else {
				addNewEmployeeEducation(employee, employeeEducationDto);
			}
		});
	}

	private void addNewEmployeeEducation(Employee employee, EmployeeEducationDto employeeEducationDto) {
		EmployeeEducation newEducation = peopleMapper.employeeEducationToEmployeeEducation(employeeEducationDto);
		newEducation.setEmployee(employee);
		employee.getEmployeeEducations().add(newEducation);
	}

	private void removeObsoleteEducations(Employee employee, List<Long> currentIdList, List<Long> updatingIdList) {
		currentIdList.forEach(item -> {
			if (!updatingIdList.contains(item)) {
				Optional<EmployeeEducation> eduOptional = employeeEducationDao.findByEducationId(item);
				eduOptional.ifPresent(education -> {
					employeeEducationDao.deleteById(education.getEducationId());
					employee.getEmployeeEducations().remove(education);
				});
			}
		});
	}

	@NotNull
	private EmployeeEducation updateEducationInfo(EmployeeEducationDto employeeEducationDto,
			Optional<EmployeeEducation> optionalEmployeeEducation) {
		if (optionalEmployeeEducation.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_EDUCATION_NOT_FOUND);
		}

		EmployeeEducation ed = optionalEmployeeEducation.get();
		ed.setDegree(employeeEducationDto.getDegree() != null ? employeeEducationDto.getDegree() : ed.getDegree());
		ed.setStartDate(
				employeeEducationDto.getStartDate() != null ? employeeEducationDto.getStartDate() : ed.getStartDate());
		ed.setEndDate(employeeEducationDto.getEndDate() != null ? employeeEducationDto.getEndDate() : ed.getEndDate());
		ed.setInstitution(employeeEducationDto.getInstitution() != null ? employeeEducationDto.getInstitution()
				: ed.getInstitution());
		ed.setSpecialization(employeeEducationDto.getSpecialization() != null ? employeeEducationDto.getSpecialization()
				: ed.getSpecialization());
		return ed;
	}

	private void updatePersonInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employee.getPersonalInfo() != null) {
			updateBasicInfo(employeeUpdateDto, employee);
			updateAddressInfo(employeeUpdateDto, employee);
			updateIdentificationInfo(employeeUpdateDto, employee);
			updateSocialMediaAndEmploymentInfo(employeeUpdateDto, employee);
		}
		else {
			createPersonalInfo(employeeUpdateDto, employee);
		}
	}

	private void updateBasicInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		EmployeePersonalInfoDto personalInfoDto = employeeUpdateDto.getEmployeePersonalInfo();
		if (personalInfoDto.getBirthDate() != null) {
			employee.getPersonalInfo().setBirthDate(personalInfoDto.getBirthDate());
		}

		if (personalInfoDto.getCity() != null) {
			employee.getPersonalInfo().setCity(personalInfoDto.getCity());
		}

		if (personalInfoDto.getEthnicity() != null) {
			employee.getPersonalInfo().setEthnicity(personalInfoDto.getEthnicity());
		}

		if (personalInfoDto.getBloodGroup() != null) {
			employee.getPersonalInfo().setBloodGroup(personalInfoDto.getBloodGroup());
		}

		if (personalInfoDto.getMaritalStatus() != null) {
			employee.getPersonalInfo().setMaritalStatus(personalInfoDto.getMaritalStatus());
		}

		if (personalInfoDto.getNationality() != null) {
			employee.getPersonalInfo().setNationality(personalInfoDto.getNationality());
		}
	}

	private void updateAddressInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		EmployeePersonalInfoDto personalInfoDto = employeeUpdateDto.getEmployeePersonalInfo();

		if (personalInfoDto.getPostalCode() != null && !personalInfoDto.getPostalCode().isBlank()) {
			employee.getPersonalInfo().setPostalCode(personalInfoDto.getPostalCode());
		}
		else if (personalInfoDto.getPostalCode() != null) {
			employee.getPersonalInfo().setPostalCode(null);
		}

		if (personalInfoDto.getState() != null) {
			employee.getPersonalInfo().setState(personalInfoDto.getState());
		}
	}

	private void updateIdentificationInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		EmployeePersonalInfoDto personalInfoDto = employeeUpdateDto.getEmployeePersonalInfo();

		if (personalInfoDto.getNin() != null) {
			employee.getPersonalInfo().setNin(personalInfoDto.getNin());
		}

		if (personalInfoDto.getPassportNo() != null && !personalInfoDto.getPassportNo().isBlank()) {
			employee.getPersonalInfo().setPassportNo(personalInfoDto.getPassportNo());
		}
		else if (personalInfoDto.getPassportNo() != null) {
			employee.getPersonalInfo().setPassportNo(null);
		}

		if (personalInfoDto.getSsn() != null && !personalInfoDto.getSsn().isBlank()) {
			employee.getPersonalInfo().setSsn(personalInfoDto.getSsn());
		}
		else if (personalInfoDto.getSsn() != null) {
			employee.getPersonalInfo().setSsn(null);
		}
	}

	private void updateSocialMediaAndEmploymentInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		EmployeePersonalInfoDto personalInfoDto = employeeUpdateDto.getEmployeePersonalInfo();

		if (personalInfoDto.getSocialMediaDetails() != null) {
			employee.getPersonalInfo().setSocialMediaDetails(personalInfoDto.getSocialMediaDetails());
		}

		if (personalInfoDto.getPreviousEmploymentDetails() != null) {
			validateEmploymentDates(personalInfoDto.getPreviousEmploymentDetails());
			employee.getPersonalInfo().setPreviousEmploymentDetails(personalInfoDto.getPreviousEmploymentDetails());
		}

		if (personalInfoDto.getExtraInfo() != null) {
			employee.getPersonalInfo().setExtraInfo(personalInfoDto.getExtraInfo());
		}
	}

	private void createPersonalInfo(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		EmployeePersonalInfo employeePersonalInfo = peopleMapper
			.employeePersonalInfoDtoToEmployeePersonalInfo(employeeUpdateDto.getEmployeePersonalInfo());
		employeePersonalInfo.setEmployee(employee);
		employee.setPersonalInfo(employeePersonalInfo);
	}

	private void updateEmployeeFamilies(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getEmployeeFamilies().isEmpty()) {
			clearEmployeeFamilies(employee);
			return;
		}

		if (employee.getEmployeeFamilies() == null || employee.getEmployeeFamilies().isEmpty()) {
			setEmployeeFamilies(employee, employeeUpdateDto.getEmployeeFamilies());
		}
		else {
			processEmployeeFamilies(employeeUpdateDto, employee);
		}
	}

	private void processEmployeeFamilies(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		List<Long> currentIdList = getCurrentFamilyIds(employee);
		List<Long> updatingIdList = new ArrayList<>();

		employeeUpdateDto.getEmployeeFamilies()
			.forEach(employeeFamilyDto -> updateOrAddFamily(employeeFamilyDto, employee, updatingIdList));

		removeUnupdatedFamilies(employee, currentIdList, updatingIdList);
	}

	private List<Long> getCurrentFamilyIds(Employee employee) {
		return employee.getEmployeeFamilies().stream().map(EmployeeFamily::getFamilyId).toList();
	}

	private void updateOrAddFamily(EmployeeFamilyDto employeeFamilyDto, Employee employee, List<Long> updatingIdList) {
		Optional<EmployeeFamily> familyOpt = findMatchingFamily(employeeFamilyDto, employee);

		if (familyOpt.isPresent()) {
			EmployeeFamily updatedFamily = updateFamilyInfo(employeeFamilyDto, familyOpt);
			updatingIdList.add(updatedFamily.getFamilyId());
		}
		else {
			addNewFamily(employeeFamilyDto, employee);
		}
	}

	private Optional<EmployeeFamily> findMatchingFamily(EmployeeFamilyDto employeeFamilyDto, Employee employee) {
		return employee.getEmployeeFamilies()
			.stream()
			.filter(fam -> employeeFamilyDto.getFamilyId() != null
					&& fam.getFamilyId().equals(employeeFamilyDto.getFamilyId()))
			.findFirst();
	}

	private void addNewFamily(EmployeeFamilyDto employeeFamilyDto, Employee employee) {
		EmployeeFamily newFamily = peopleMapper.employeeFamilyDtoToEmployeeFamily(employeeFamilyDto);
		newFamily.setEmployee(employee);
		employee.getEmployeeFamilies().add(newFamily);
	}

	private void removeUnupdatedFamilies(Employee employee, List<Long> currentIdList, List<Long> updatingIdList) {
		currentIdList.stream()
			.filter(item -> !updatingIdList.contains(item))
			.forEach(item -> removeFamilyById(employee, item));
	}

	private void removeFamilyById(Employee employee, Long familyId) {
		Optional<EmployeeFamily> famOptional = employeeFamilyDao.findByFamilyId(familyId);
		famOptional.ifPresent(family -> {
			employeeFamilyDao.deleteById(family.getFamilyId());
			employee.getEmployeeFamilies().remove(family);
		});
	}

	private void clearEmployeeFamilies(Employee employee) {
		employee.getEmployeeFamilies().forEach(family -> employeeFamilyDao.deleteById(family.getFamilyId()));
		employee.getEmployeeFamilies().clear();
	}

	@NotNull
	private EmployeeFamily updateFamilyInfo(EmployeeFamilyDto employeeFamilyDto,
			Optional<EmployeeFamily> optionalEmployeeFamily) {
		if (optionalEmployeeFamily.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_FAMILY_DETAILS_NOT_FOUND);
		}

		EmployeeFamily famDetail = optionalEmployeeFamily.get();
		famDetail.setBirthDate(
				employeeFamilyDto.getBirthDate() != null ? employeeFamilyDto.getBirthDate() : famDetail.getBirthDate());
		famDetail.setFamilyRelationship(employeeFamilyDto.getFamilyRelationship() != null
				? employeeFamilyDto.getFamilyRelationship() : famDetail.getFamilyRelationship());
		famDetail
			.setGender(employeeFamilyDto.getGender() != null ? employeeFamilyDto.getGender() : famDetail.getGender());
		famDetail.setFirstName(
				employeeFamilyDto.getFirstName() != null ? employeeFamilyDto.getFirstName() : famDetail.getFirstName());
		famDetail.setLastName(
				employeeFamilyDto.getLastName() != null ? employeeFamilyDto.getLastName() : famDetail.getLastName());
		famDetail.setParentName(employeeFamilyDto.getParentName() != null ? employeeFamilyDto.getParentName()
				: famDetail.getParentName());
		return famDetail;
	}

	private EmployeeTimeline getEmployeeTimeline(Employee employee, EmployeeTimelineType timelineType, String title,
			String previousValue, String newValue) {
		EmployeeTimeline timeline = new EmployeeTimeline();
		timeline.setEmployee(employee);
		timeline.setTimelineType(timelineType);
		timeline.setTitle(title);
		timeline.setPreviousValue(previousValue);
		timeline.setNewValue(newValue);
		timeline.setDisplayDate(DateTimeUtils.getCurrentUtcDate());
		return timeline;
	}

	private void updateEmployeeTeam(Set<Long> teamIds, Employee employee) {
		List<Team> teams = teamDao.findAllById(teamIds);

		Set<EmployeeTeam> currentEmployeeTeams = employee.getTeams();

		List<Team> currentTeams = currentEmployeeTeams.stream().map(EmployeeTeam::getTeam).toList();
		List<Team> teamsToAdd = new ArrayList<>(teams);
		teamsToAdd.removeIf(currentTeams::contains);

		List<Team> teamsToRemove = new ArrayList<>(currentTeams);
		teamsToRemove.removeIf(teams::contains);
		List<EmployeeTeam> employeeTeamsToRemove = new ArrayList<>();
		if (!teamsToRemove.isEmpty()) {
			employeeTeamsToRemove = currentEmployeeTeams.stream()
				.filter(empTeam -> teamsToRemove.contains(empTeam.getTeam()))
				.toList();
			employeeTeamDao.deleteAllInBatch(employeeTeamsToRemove);
			List<EmployeeTimeline> timeLineNewTeamList = new ArrayList<>();
			for (Team team : teamsToRemove) {
				timeLineNewTeamList.add(getEmployeeTimeline(employee, EmployeeTimelineType.TEAM_REMOVED,
						EmployeeTimelineConstant.TITLE_TEAM_REMOVED, team.getTeamName(), null));
			}
			employeeTimelineDao.saveAll(timeLineNewTeamList);
		}

		Set<EmployeeTeam> newEmployeeTeams = new HashSet<>();
		if (!teamsToAdd.isEmpty()) {
			List<EmployeeTimeline> timeLineNewTeamList = new ArrayList<>();

			for (Team team : teamsToAdd) {
				EmployeeTeam employeeTeam = new EmployeeTeam();
				employeeTeam.setTeam(team);
				employeeTeam.setEmployee(employee);
				newEmployeeTeams.add(employeeTeam);
				employeeTeam.setIsSupervisor(false);
				timeLineNewTeamList.add(getEmployeeTimeline(employee, EmployeeTimelineType.TEAM_ASSIGNED,
						PeopleConstants.TITLE_TEAM_CHANGED, null, team.getTeamName()));
			}
			employeeTeamDao.saveAll(newEmployeeTeams);
			employeeTimelineDao.saveAll(timeLineNewTeamList);
		}
		else {
			log.info("updateEmployee: no teams to assign");
		}

		Set<EmployeeTeam> finalTeams = new HashSet<>();
		finalTeams.addAll(currentEmployeeTeams);
		finalTeams.addAll(newEmployeeTeams);
		finalTeams.removeIf(employeeTeamsToRemove::contains);
		employee.setTeams(finalTeams);
	}

	private void updateManagers(EmployeeUpdateDto employeeUpdateDto, Employee employee) {
		if (employeeUpdateDto.getPrimaryManager() != null && employeeUpdateDto.getSecondaryManager() != null
				&& Objects.equals(employeeUpdateDto.getPrimaryManager(), employeeUpdateDto.getSecondaryManager())) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_SECONDARY_MANAGER_DUPLICATE);
		}

		List<EmployeeManager> employeeManagers = employeeManagerDao.findByEmployee(employee);

		Optional<EmployeeManager> employeePrimaryManagers = employeeManagers.stream()
			.filter(manager -> manager.getManagerType().equals(ManagerType.PRIMARY))
			.findFirst();

		Optional<EmployeeManager> employeeSecondaryManagers = employeeManagers.stream()
			.filter(manager -> manager.getManagerType().equals(ManagerType.SECONDARY))
			.findFirst();

		if ((employeeUpdateDto.getPrimaryManager() == null && employeePrimaryManagers.isEmpty())
				&& (employeeUpdateDto.getSecondaryManager() == null && employeeSecondaryManagers.isEmpty())) {
			return;
		}

		if (employeePrimaryManagers.isPresent() && employeeSecondaryManagers.isPresent()
				&& employeeUpdateDto.getPrimaryManager() != null && employeeUpdateDto.getSecondaryManager() != null) {
			if ((Objects.equals(employeePrimaryManagers.get().getManager().getEmployeeId(),
					employeeUpdateDto.getPrimaryManager()))
					&& (Objects.equals(employeeSecondaryManagers.get().getManager().getEmployeeId(),
							employeeUpdateDto.getSecondaryManager()))) {
				return;
			}
		}

		if (employeeUpdateDto.getPrimaryManager() == null) {
			if (employeePrimaryManagers.isPresent()) {
				employeeManagerDao.deleteByEmployeeAndManagerType(employee, ManagerType.PRIMARY);
			}
			employeeUpdateDto.setSecondaryManager(null);
		}

		if (employeeUpdateDto.getSecondaryManager() == null) {
			if (employeeSecondaryManagers.isPresent()) {
				employeeManagerDao.deleteByEmployeeAndManagerType(employee, ManagerType.SECONDARY);
			}
		}

		if (employeeUpdateDto.getPrimaryManager() != null) {
			if (employeePrimaryManagers.isPresent()) {
				employeeManagerDao.deleteByEmployeeAndManagerType(employee, ManagerType.PRIMARY);
			}

			Employee primaryManager = employeeDao
				.findEmployeeByEmployeeIdAndUserIsActiveTrue(employeeUpdateDto.getPrimaryManager());

			EmployeeManager newPrimaryManager = new EmployeeManager();
			newPrimaryManager.setEmployee(employee);
			newPrimaryManager.setManagerType(ManagerType.PRIMARY);
			newPrimaryManager.setManager(primaryManager);
			newPrimaryManager.setPrimaryManager(true);
			employeeManagerDao.save(newPrimaryManager);
		}

		if (employeeUpdateDto.getSecondaryManager() != null) {
			if (employeeSecondaryManagers.isPresent()) {
				employeeManagerDao.deleteByEmployeeAndManagerType(employee, ManagerType.SECONDARY);
			}

			Employee secondaryManager = employeeDao
				.findEmployeeByEmployeeIdAndUserIsActiveTrue(employeeUpdateDto.getSecondaryManager());

			EmployeeManager newSecondaryManager = new EmployeeManager();
			newSecondaryManager.setEmployee(employee);
			newSecondaryManager.setManagerType(ManagerType.SECONDARY);
			newSecondaryManager.setManager(secondaryManager);
			newSecondaryManager.setPrimaryManager(false);
			employeeManagerDao.save(newSecondaryManager);
		}
	}

	private void updateEmployeeProgression(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeUpdateDto.getEmployeeProgressions().isEmpty()) {
			clearEmployeeProgression(employee);
			return;
		}
		JobFamily previousJobFamily = employee.getJobFamily() != null ? employee.getJobFamily() : null;
		JobTitle previousJobTitle = employee.getJobTitle() != null ? employee.getJobTitle() : null;
		validateCareerProgressionData(employeeUpdateDto.getEmployeeProgressions());
		saveCareerProgression(employee, employeeUpdateDto.getEmployeeProgressions());
		updateCareerProgressionTimeline(employeeUpdateDto, employee, employeeTimelines, previousJobTitle,
				previousJobFamily);
	}

	private void clearEmployeeProgression(Employee employee) {
		List<Long> currentIdList = employee.getEmployeeProgressions()
			.stream()
			.map(EmployeeProgression::getProgressionId)
			.toList();
		currentIdList.forEach(item -> {
			Optional<EmployeeProgression> progressionOptional = employeeProgressionDao.findByProgressionId(item);
			if (progressionOptional.isPresent()) {
				employeeProgressionDao.deleteById(progressionOptional.get().getProgressionId());
				employee.getEmployeeProgressions().remove(progressionOptional.get());
			}
		});
	}

	private void updateCareerProgressionTimeline(EmployeeUpdateDto employeeUpdateDto, Employee employee,
			List<EmployeeTimeline> employeeTimelines, JobTitle previousJobTitle, JobFamily previousJobFamily) {
		Optional<EmployeeProgressionsDto> currentEmployeeProgression = employeeUpdateDto.getEmployeeProgressions()
			.stream()
			.filter(EmployeeProgressionsDto::getIsCurrent)
			.findFirst();

		if (currentEmployeeProgression.isPresent()) {
			if (currentEmployeeProgression.get().getJobTitleId() != null) {
				if (previousJobTitle == null && employee.getJobTitle() != null) {
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.JOB_FAMILY_CHANGED,
							EmployeeTimelineConstant.TITLE_JOB_TITLE_ASSIGNED, null, employee.getJobTitle().getName()));
				}
				else if (previousJobTitle != null && previousJobTitle.getJobTitleId() != null
						&& !previousJobTitle.getJobTitleId().equals(currentEmployeeProgression.get().getJobTitleId())
						&& employee.getJobTitle() != null) {
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.JOB_FAMILY_CHANGED,
							TITLE_JOB_TITLE_CHANGED, previousJobTitle.getName(), employee.getJobTitle().getName()));
				}
			}

			if (currentEmployeeProgression.get().getJobFamilyId() != null) {
				if (previousJobFamily == null && employee.getJobFamily() != null) {
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.DEPARTMENT_CHANGED,
							TITLE_JOB_FAMILY_CHANGED, null, employee.getJobFamily().getName()));
				}
				else if (previousJobFamily != null && previousJobFamily.getJobFamilyId() != null
						&& !previousJobFamily.getJobFamilyId()
							.equals(currentEmployeeProgression.get().getJobFamilyId())) {
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.DEPARTMENT_CHANGED,
							TITLE_JOB_FAMILY_CHANGED, previousJobFamily.getName(), employee.getJobFamily().getName()));
				}
			}

		}
	}

	private void modifyManagerEmployeesHistory(String employeePreviousName, String employeePreviousLastName,
			Employee employee) {
		boolean isEmployeeOnly = employee.getEmployeeRole().getAttendanceRole().equals(Role.ATTENDANCE_EMPLOYEE)
				|| employee.getEmployeeRole().getLeaveRole().equals(Role.LEAVE_EMPLOYEE)
				|| employee.getEmployeeRole().getPeopleRole().equals(Role.PEOPLE_EMPLOYEE);
		if ((!isEmployeeOnly && (!employeePreviousName.equals(employee.getFirstName())
				|| !employeePreviousLastName.equals(employee.getLastName())))) {

			employee.getManagers().forEach(assignedEmployee -> {
				String managerTypeTitle = null;
				if (!assignedEmployee.getManagerType().equals(ManagerType.INFORMANT)) {
					if (assignedEmployee.getManagerType().equals(ManagerType.PRIMARY)) {
						managerTypeTitle = EmployeeTimelineConstant.TITLE_PRIMARY_SUPERVISOR_NAME_CHANGED;
					}
					else if (assignedEmployee.getManagerType().equals(ManagerType.SECONDARY)) {
						managerTypeTitle = EmployeeTimelineConstant.TITLE_SECONDARY_SUPERVISOR_NAME_CHANGED;
					}

					employeeTimelineService.addEmployeeTimelineRecord(assignedEmployee.getEmployee(),
							EmployeeTimelineType.MANAGER_CHANGED, managerTypeTitle,
							employeePreviousName + " " + employeePreviousLastName,
							employee.getFirstName() + " " + employee.getLastName());
				}
			});
		}
	}

	private void setEmployeePeriodDto(EmployeeDetailedResponseDto employeeResponseDto) {
		employeeResponseDto.setPeriodResponseDto(employeeResponseDto.getPeriodResponseDto());
	}

	private Set<EmployeeTeam> getEmployeeTeamsByName(Set<String> teamName, Employee finalEmployee) {
		List<Team> teams = teamDao.findAllByTeamNameIn(teamName);

		if (teamName.size() != teams.size()) {
			log.info("addNewEmployee: Team ID(s) are not valid");
		}

		Set<EmployeeTeam> employeeTeams;
		if (!teams.isEmpty()) {
			employeeTeams = teams.parallelStream().map(team -> {
				EmployeeTeam employeeTeam = new EmployeeTeam();
				employeeTeam.setTeam(team);
				employeeTeam.setEmployee(finalEmployee);
				employeeTeam.setIsSupervisor(false);
				return employeeTeam;
			}).collect(Collectors.toSet());
		}
		else {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_TEAM_NOT_FOUND);
		}
		return employeeTeams;
	}

	private void validateRoles(RoleRequestDto userRoles) {
		User currentUser = userService.getCurrentUser();

		if (hasOnlyAdminPermissions(currentUser) && ((userRoles.getAttendanceRole() != null
				&& validateRestrictedRoleAssignment(userRoles.getAttendanceRole(), ModuleType.ATTENDANCE))
				|| (userRoles.getPeopleRole() != null
						&& validateRestrictedRoleAssignment(userRoles.getPeopleRole(), ModuleType.PEOPLE))
				|| (userRoles.getLeaveRole() != null
						&& validateRestrictedRoleAssignment(userRoles.getLeaveRole(), ModuleType.LEAVE)))) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_SUPER_ADMIN_RESTRICTED_ASSIGNING_ROLE_ACCESS);
		}

		if (Boolean.TRUE.equals(userRoles.getIsSuperAdmin())
				&& (userRoles.getPeopleRole() != Role.PEOPLE_ADMIN || userRoles.getLeaveRole() != Role.LEAVE_ADMIN
						|| userRoles.getAttendanceRole() != Role.ATTENDANCE_ADMIN)) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_SHOULD_ASSIGN_PROPER_PERMISSIONS);
		}
	}

	private boolean hasOnlyAdminPermissions(User currentUser) {
		return Boolean.FALSE.equals(currentUser.getEmployee().getEmployeeRole().getIsSuperAdmin())
				&& currentUser.getEmployee().getEmployeeRole().getPeopleRole() == Role.PEOPLE_ADMIN;
	}

	private Boolean validateRestrictedRoleAssignment(Role role, ModuleType moduleType) {
		ModuleRoleRestrictionResponseDto restrictedRole = rolesService.getRestrictedRoleByModule(moduleType);

		return switch (role) {
			case PEOPLE_ADMIN, ATTENDANCE_ADMIN, LEAVE_ADMIN -> Boolean.TRUE.equals(restrictedRole.getIsAdmin());
			case PEOPLE_MANAGER, ATTENDANCE_MANAGER, LEAVE_MANAGER ->
				Boolean.TRUE.equals(restrictedRole.getIsManager());
			default -> false;
		};

	}

}
