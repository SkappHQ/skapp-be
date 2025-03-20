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
import com.skapp.community.common.service.UserVersionService;
import com.skapp.community.common.service.impl.AsyncEmailServiceImpl;
import com.skapp.community.common.type.LoginMethod;
import com.skapp.community.common.type.NotificationSettingsType;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.type.VersionType;
import com.skapp.community.common.util.CommonModuleUtils;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.common.util.Validation;
import com.skapp.community.common.util.event.UserCreatedEvent;
import com.skapp.community.common.util.event.UserDeactivatedEvent;
import com.skapp.community.common.util.transformer.PageTransformer;
import com.skapp.community.leaveplanner.type.ManagerType;
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
import com.skapp.community.peopleplanner.model.EmployeeVisa;
import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.model.JobTitle;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.peopleplanner.payload.CurrentEmployeeDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeBulkDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDataValidationDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeExportFilterDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeFilterDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeProgressionsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeQuickAddDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.JobTitleDto;
import com.skapp.community.peopleplanner.payload.request.NotificationSettingsPatchRequestDto;
import com.skapp.community.peopleplanner.payload.request.PermissionFilterDto;
import com.skapp.community.peopleplanner.payload.request.ProbationPeriodDto;
import com.skapp.community.peopleplanner.payload.request.employee.CreateEmployeeRequestDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeEmploymentDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeePersonalDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeSystemPermissionsDto;
import com.skapp.community.peopleplanner.payload.request.employee.emergency.EmployeeEmergencyContactDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeeExtraInfoDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalSocialMediaDetailsDto;
import com.skapp.community.peopleplanner.payload.response.AnalyticsSearchResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeAllDataExportResponseDto;
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
import com.skapp.community.peopleplanner.payload.response.EmployeeTeamDto;
import com.skapp.community.peopleplanner.payload.response.ManagerEmployeeDto;
import com.skapp.community.peopleplanner.payload.response.ManagingEmployeesResponseDto;
import com.skapp.community.peopleplanner.payload.response.PrimarySecondaryOrTeamSupervisorResponseDto;
import com.skapp.community.peopleplanner.payload.response.SummarizedEmployeeDtoForEmployees;
import com.skapp.community.peopleplanner.payload.response.SummarizedManagerEmployeeDto;
import com.skapp.community.peopleplanner.payload.response.TeamDetailResponseDto;
import com.skapp.community.peopleplanner.payload.response.TeamEmployeeResponseDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeManagerDao;
import com.skapp.community.peopleplanner.repository.EmployeePeriodDao;
import com.skapp.community.peopleplanner.repository.EmployeeTeamDao;
import com.skapp.community.peopleplanner.repository.JobFamilyDao;
import com.skapp.community.peopleplanner.repository.JobTitleDao;
import com.skapp.community.peopleplanner.repository.TeamDao;
import com.skapp.community.peopleplanner.service.PeopleEmailService;
import com.skapp.community.peopleplanner.service.PeopleService;
import com.skapp.community.peopleplanner.service.RolesService;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.BulkItemStatus;
import com.skapp.community.peopleplanner.type.EmploymentType;
import com.skapp.community.peopleplanner.util.Validations;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.Arrays;
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
import java.util.stream.Collectors;

import static com.skapp.community.common.util.Validation.ADDRESS_REGEX;
import static com.skapp.community.common.util.Validation.ALPHANUMERIC_REGEX;
import static com.skapp.community.common.util.Validation.NAME_REGEX;
import static com.skapp.community.common.util.Validation.SPECIAL_CHAR_REGEX;
import static com.skapp.community.common.util.Validation.VALID_NIN_NUMBER_REGEXP;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService {

	private final UserService userService;

	private final MessageUtil messageUtil;

	private final PeopleMapper peopleMapper;

	private final UserDao userDao;

	private final TeamDao teamDao;

	private final EmployeeDao employeeDao;

	private final JobFamilyDao jobFamilyDao;

	private final JobTitleDao jobTitleDao;

	private final EmployeePeriodDao employeePeriodDao;

	private final EmployeeTeamDao employeeTeamDao;

	private final EmployeeManagerDao employeeManagerDao;

	private final PasswordEncoder passwordEncoder;

	private final RolesService rolesService;

	private final PageTransformer pageTransformer;

	private final PlatformTransactionManager transactionManager;

	private final PeopleEmailService peopleEmailService;

	private final ObjectMapper mapper;

	private final EncryptionDecryptionService encryptionDecryptionService;

	private final BulkContextService bulkContextService;

	private final AsyncEmailServiceImpl asyncEmailServiceImpl;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final UserVersionService userVersionService;

	@Value("${encryptDecryptAlgorithm.secret}")
	private String encryptSecret;

	@Override
	@Transactional
	public ResponseEntityDto createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto) {
		validateCreateEmployeeRequestRequiredFields(createEmployeeRequestDto);
		validateCreateEmployeeRequestPersonalDetails(createEmployeeRequestDto.getPersonal());
		validateCreateEmployeeRequestEmploymentDetails(createEmployeeRequestDto.getEmployment());

		User user = new User();
		Employee employee = new Employee();
		EmployeePersonalInfo employeePersonalInfo = new EmployeePersonalInfo();
		List<EmployeeEmergency> emergencyContacts = new ArrayList<>();
		List<EmployeeVisa> employeeVisas = new ArrayList<>();
		Set<EmployeePeriod> employeePeriods = new HashSet<>();
		Set<EmployeeTeam> employeeTeams = new HashSet<>();
		Set<EmployeeManager> employeeManagers = new HashSet<>();

		// General
		employee.setFirstName(createEmployeeRequestDto.getPersonal().getGeneral().getFirstName());
		employee.setMiddleName(createEmployeeRequestDto.getPersonal().getGeneral().getMiddleName());
		employee.setLastName(createEmployeeRequestDto.getPersonal().getGeneral().getLastName());
		employee.setGender(createEmployeeRequestDto.getPersonal().getGeneral().getGender());
		employeePersonalInfo.setBirthDate(createEmployeeRequestDto.getPersonal().getGeneral().getDateOfBirth());
		employeePersonalInfo.setNationality(createEmployeeRequestDto.getPersonal().getGeneral().getNationality());
		employeePersonalInfo.setNin(createEmployeeRequestDto.getPersonal().getGeneral().getNin());
		employeePersonalInfo.setPassportNo(createEmployeeRequestDto.getPersonal().getGeneral().getPassportNumber());
		employeePersonalInfo.setMaritalStatus(createEmployeeRequestDto.getPersonal().getGeneral().getMaritalStatus());

		// Contact
		if (createEmployeeRequestDto.getPersonal().getContact() != null) {
			employee.setPersonalEmail(createEmployeeRequestDto.getPersonal().getContact().getPersonalEmail());
			employee.setPhone(createEmployeeRequestDto.getPersonal().getContact().getContactNo());
			employee.setAddressLine1(createEmployeeRequestDto.getPersonal().getContact().getAddressLine1());
			employee.setAddressLine2(createEmployeeRequestDto.getPersonal().getContact().getAddressLine2());
			employeePersonalInfo.setCity(createEmployeeRequestDto.getPersonal().getContact().getCity());
			employeePersonalInfo.setState(createEmployeeRequestDto.getPersonal().getContact().getState());
			employee.setCountry(createEmployeeRequestDto.getPersonal().getContact().getCountry());
			employeePersonalInfo.setPostalCode(createEmployeeRequestDto.getPersonal().getContact().getPostalCode());
		}

		// Family
		if (createEmployeeRequestDto.getPersonal().getFamily() != null
				&& !createEmployeeRequestDto.getPersonal().getFamily().isEmpty()) {
			employee.setEmployeeFamilies(createEmployeeRequestDto.getPersonal().getFamily().stream().map(familyDto -> {
				EmployeeFamily family = new EmployeeFamily();
				family.setFirstName(familyDto.getFirstName());
				family.setLastName(familyDto.getLastName());
				family.setGender(familyDto.getGender());
				family.setFamilyRelationship(familyDto.getRelationship());
				family.setBirthDate(familyDto.getDateOfBirth());
				family.setParentName(familyDto.getParentName());
				family.setEmployee(employee);
				return family;
			}).toList());
		}

		// Educational
		if (createEmployeeRequestDto.getPersonal().getEducational() != null
				&& !createEmployeeRequestDto.getPersonal().getEducational().isEmpty()) {
			employee.setEmployeeEducations(
					createEmployeeRequestDto.getPersonal().getEducational().stream().map(educationDto -> {
						EmployeeEducation education = new EmployeeEducation();
						education.setInstitution(educationDto.getInstitutionName());
						education.setDegree(educationDto.getDegree());
						education.setSpecialization(educationDto.getMajor());
						education.setStartDate(educationDto.getStartDate());
						education.setEndDate(educationDto.getEndDate());
						education.setEmployee(employee);
						return education;
					}).toList());
		}

		// Social Media
		EmployeePersonalSocialMediaDetailsDto socialMedia = createEmployeeRequestDto.getPersonal().getSocialMedia();
		if (socialMedia == null) {
			socialMedia = new EmployeePersonalSocialMediaDetailsDto();
		}
		JsonNode socialMediaJson = mapper.valueToTree(socialMedia);
		employeePersonalInfo.setSocialMediaDetails(socialMediaJson);

		// Health and Other
		EmployeeExtraInfoDto employeeExtraInfoDto = new EmployeeExtraInfoDto();
		if (createEmployeeRequestDto.getPersonal().getHealthAndOther() != null) {
			employeePersonalInfo
				.setBloodGroup(createEmployeeRequestDto.getPersonal().getHealthAndOther().getBloodGroup());
			employeeExtraInfoDto
				.setAllergies(createEmployeeRequestDto.getPersonal().getHealthAndOther().getAllergies());
			employeeExtraInfoDto
				.setTShirtSize(createEmployeeRequestDto.getPersonal().getHealthAndOther().getTShirtSize());
			employeeExtraInfoDto.setDietaryRestrictions(
					createEmployeeRequestDto.getPersonal().getHealthAndOther().getDietaryRestrictions());
		}

		JsonNode extraInfoJson = mapper.valueToTree(employeeExtraInfoDto);
		employeePersonalInfo.setExtraInfo(extraInfoJson);

		// Emergency
		if (createEmployeeRequestDto.getEmergency() != null) {
			// Create primary emergency contact
			if (createEmployeeRequestDto.getEmergency().getPrimaryEmergencyContact() != null) {
				emergencyContacts.add(createEmployeeEmergency(
						createEmployeeRequestDto.getEmergency().getPrimaryEmergencyContact(), true, employee));
			}

			// Create secondary emergency contact
			if (createEmployeeRequestDto.getEmergency().getSecondaryEmergencyContact() != null) {
				emergencyContacts.add(createEmployeeEmergency(
						createEmployeeRequestDto.getEmergency().getSecondaryEmergencyContact(), false, employee));
			}
		}

		// Employment Details
		employee
			.setIdentificationNo(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmployeeNumber());
		user.setEmail(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmail());
		employee.setEmploymentAllocation(
				createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmploymentAllocation());
		employee.setJoinDate(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getJoinedDate());
		employee.setTimeZone(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getWorkTimeZone());

		if (createEmployeeRequestDto.getEmployment().getEmploymentDetails().getTeamIds() != null
				&& createEmployeeRequestDto.getEmployment().getEmploymentDetails().getTeamIds().length > 0) {

			employeeTeams = Arrays.stream(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getTeamIds())
				.map(teamId -> {
					Team team = teamDao.findByTeamId(teamId);

					EmployeeTeam employeeTeam = new EmployeeTeam();
					employeeTeam.setTeam(team);
					employeeTeam.setIsSupervisor(false);
					employeeTeam.setEmployee(employee);
					return employeeTeam;
				})
				.collect(Collectors.toSet());
		}

		// Primary Supervisor
		if (createEmployeeRequestDto.getEmployment().getEmploymentDetails().getPrimarySupervisor() != null
				&& createEmployeeRequestDto.getEmployment()
					.getEmploymentDetails()
					.getPrimarySupervisor()
					.getEmployeeId() != null) {
			EmployeeManager primarySupervisor = new EmployeeManager();
			Employee primaryManager = employeeDao.findEmployeeByEmployeeId(createEmployeeRequestDto.getEmployment()
				.getEmploymentDetails()
				.getPrimarySupervisor()
				.getEmployeeId());
			primarySupervisor.setManager(primaryManager);
			primarySupervisor.setEmployee(employee);
			primarySupervisor.setManagerType(ManagerType.PRIMARY);
			primarySupervisor.setIsPrimaryManager(true);
			employeeManagers.add(primarySupervisor);
		}

		// Secondary Supervisor
		if (createEmployeeRequestDto.getEmployment().getEmploymentDetails().getSecondarySupervisor() != null
				&& createEmployeeRequestDto.getEmployment()
					.getEmploymentDetails()
					.getSecondarySupervisor()
					.getEmployeeId() != null) {
			EmployeeManager secondarySupervisor = new EmployeeManager();
			Employee secondaryManager = employeeDao.findEmployeeByEmployeeId(createEmployeeRequestDto.getEmployment()
				.getEmploymentDetails()
				.getSecondarySupervisor()
				.getEmployeeId());
			secondarySupervisor.setManager(secondaryManager);
			secondarySupervisor.setEmployee(employee);
			secondarySupervisor.setManagerType(ManagerType.SECONDARY);
			secondarySupervisor.setIsPrimaryManager(false);
			employeeManagers.add(secondarySupervisor);
		}

		// Employee Probation Period
		EmployeePeriod employeePeriod = new EmployeePeriod();
		employeePeriod
			.setStartDate(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getProbationStartDate());
		employeePeriod
			.setEndDate(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getProbationEndDate());
		employeePeriod.setIsActive(true);
		employeePeriod.setEmployee(employee);
		employeePeriods.add(employeePeriod);

		// Career Progression
		if (createEmployeeRequestDto.getEmployment().getCareerProgression() != null
				&& !createEmployeeRequestDto.getEmployment().getCareerProgression().isEmpty()) {
			List<EmployeeProgression> progressions = createEmployeeRequestDto.getEmployment()
				.getCareerProgression()
				.stream()
				.map(progression -> {
					EmployeeProgression employeeProgression = new EmployeeProgression();
					employeeProgression.setEmploymentType(progression.getEmploymentType());
					employeeProgression.setJobFamilyId(progression.getJobFamilyId());
					employeeProgression.setJobTitleId(progression.getJobTitleId());
					employeeProgression.setStartDate(progression.getStartDate());
					employeeProgression.setEndDate(progression.getEndDate());
					employeeProgression.setIsCurrent(progression.getIsCurrentEmployment());
					if (Boolean.TRUE.equals(progression.getIsCurrentEmployment())) {
						employee.setEmploymentType(progression.getEmploymentType());
						employee.setJobFamily(jobFamilyDao.getJobFamilyById(progression.getJobFamilyId()));
						employee.setJobTitle(jobTitleDao.getJobTitleById(progression.getJobTitleId()));
					}
					employeeProgression.setEmployee(employee);
					return employeeProgression;
				})
				.collect(Collectors.toList());

			employee.setEmployeeProgressions(progressions);
		}

		// Identification and Diversity Details
		if (createEmployeeRequestDto.getEmployment().getIdentificationAndDiversityDetails() != null) {
			employeePersonalInfo
				.setSsn(createEmployeeRequestDto.getEmployment().getIdentificationAndDiversityDetails().getSsn());
			employee.setEeo(createEmployeeRequestDto.getEmployment()
				.getIdentificationAndDiversityDetails()
				.getEeoJobCategory());
			employeePersonalInfo.setEthnicity(
					createEmployeeRequestDto.getEmployment().getIdentificationAndDiversityDetails().getEthnicity());
		}

		// Previous Employment Details
		if (createEmployeeRequestDto.getEmployment().getPreviousEmployment() != null) {
			JsonNode previousEmploymentDetails = mapper
				.valueToTree(createEmployeeRequestDto.getEmployment().getPreviousEmployment());
			employeePersonalInfo.setPreviousEmploymentDetails(previousEmploymentDetails);
		}

		// Visa Details
		if (createEmployeeRequestDto.getEmployment().getVisaDetails() != null
				&& !createEmployeeRequestDto.getEmployment().getVisaDetails().isEmpty()) {
			employeeVisas = createEmployeeRequestDto.getEmployment().getVisaDetails().stream().map(visaDetail -> {
				EmployeeVisa employeeVisa = new EmployeeVisa();
				employeeVisa.setVisaType(visaDetail.getVisaType());
				employeeVisa.setIssuingCountry(visaDetail.getIssuingCountry());
				employeeVisa.setIssuedDate(visaDetail.getIssuedDate());
				employeeVisa.setExpirationDate(visaDetail.getExpiryDate());
				employeeVisa.setEmployee(employee);
				return employeeVisa;
			}).toList();
		}

		// Common
		if (createEmployeeRequestDto.getCommon() != null) {
			employee.setAuthPic(createEmployeeRequestDto.getCommon().getAuthPic());
		}

		// System Permissions
		EmployeeRole employeeRole = rolesService.assignRolesToEmployee(createEmployeeRequestDto.getSystemPermissions(),
				employee);

		// User Settings
		UserSettings userSettings = createNotificationSettings(createEmployeeRequestDto.getSystemPermissions(), user);

		// Password Ge
		String tempPassword = CommonModuleUtils.generateSecureRandomPassword();
		Optional<User> firstUser = userDao.findById(1L);
		LoginMethod loginMethod = firstUser.isPresent() ? firstUser.get().getLoginMethod() : LoginMethod.CREDENTIALS;

		if (loginMethod.equals(LoginMethod.GOOGLE)) {
			user.setIsPasswordChangedForTheFirstTime(true);
		}

		if (loginMethod.equals(LoginMethod.CREDENTIALS)) {
			user.setTempPassword(encryptionDecryptionService.encrypt(tempPassword, encryptSecret));
			user.setPassword(passwordEncoder.encode(tempPassword));
			user.setIsPasswordChangedForTheFirstTime(false);
		}

		employeePersonalInfo.setEmployee(employee);

		employee.setAccountStatus(AccountStatus.PENDING);
		employee.setPersonalInfo(employeePersonalInfo);
		employee.setEmployeeEmergencies(emergencyContacts);
		employee.setEmployeeTeams(employeeTeams);
		employee.setEmployeeRole(employeeRole);
		employee.setEmployeeManagers(employeeManagers);
		employee.setEmployeeVisas(employeeVisas);
		employee.setEmployeePeriods(employeePeriods);
		employee.setUser(user);

		user.setIsActive(true);
		user.setLoginMethod(loginMethod);
		user.setSettings(userSettings);
		user.setEmployee(employee);

		userDao.save(user);
		applicationEventPublisher.publishEvent(new UserCreatedEvent(this, user));

		// Send User Invitation Email
		peopleEmailService.sendUserInvitationEmail(user);

		// Add Timeline Records
		addNewEmployeeTimeLineRecords(employee, createEmployeeRequestDto);

		// Update Subscription Quantity
		updateSubscriptionQuantity(1L, true);

		return new ResponseEntityDto(false, createEmployeeRequestDto);
	}

	@Override
	public ResponseEntityDto quickAddEmployee(EmployeeQuickAddDto employeeQuickAddDto) {
		validateQuickAddEmployeeRequestRequiredFields(employeeQuickAddDto);

		User user = new User();
		Employee employee = peopleMapper.employeeQuickAddDtoToEmployee(employeeQuickAddDto);

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

		UserSettings userSettings = createNotificationSettings(employeeQuickAddDto.getUserRoles(), user);

		employee.setAccountStatus(AccountStatus.PENDING);
		employee.setUser(user);

		user.setEmail(employeeQuickAddDto.getEmail());
		user.setIsActive(true);
		user.setEmployee(employee);
		user.setSettings(userSettings);

		userDao.save(user);
		applicationEventPublisher.publishEvent(new UserCreatedEvent(this, user));

		rolesService.assignRolesToEmployee(employeeQuickAddDto.getUserRoles(), employee);

		peopleEmailService.sendUserInvitationEmail(employee.getUser());
		addNewQuickUploadedEmployeeTimeLineRecords(employee, employeeQuickAddDto);
		updateSubscriptionQuantity(1L, true);

		EmployeeDetailedResponseDto employeeResponseDto = peopleMapper.employeeToEmployeeDetailedResponseDto(employee);
		EmployeeCredentialsResponseDto employeeCredentials = new EmployeeCredentialsResponseDto();
		employeeCredentials.setEmail(employee.getUser().getEmail());
		employeeCredentials.setTempPassword(tempPassword);

		employeeResponseDto.setEmployeeCredentials(employeeCredentials);

		return new ResponseEntityDto(false, employeeResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto updateEmployee(Long employeeId, CreateEmployeeRequestDto createEmployeeRequestDto) {
		validateCreateEmployeeRequestRequiredFields(createEmployeeRequestDto);
		validateCreateEmployeeRequestPersonalDetails(createEmployeeRequestDto.getPersonal());
		validateCreateEmployeeRequestEmploymentDetails(createEmployeeRequestDto.getEmployment());

		return null;
	}

	@Override
	@Transactional
	public ResponseEntityDto getEmployees(EmployeeFilterDto employeeFilterDto) {
		log.info("getEmployees: execution started");
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

			log.info("getEmployees: Successfully executed");
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
	public ResponseEntityDto exportEmployees(EmployeeExportFilterDto employeeExportFilterDto) {
		User currentUser = userService.getCurrentUser();
		log.info("exportEmployees: execution started by user: {}", currentUser.getUserId());

		List<Employee> employees = employeeDao.findEmployeesForExport(employeeExportFilterDto);

		List<Long> employeeIds = employees.stream().map(Employee::getEmployeeId).toList();
		List<EmployeeTeamDto> teamList = employeeDao.findTeamsByEmployees(employeeIds);

		List<EmployeeAllDataExportResponseDto> responseDtos = exportAllEmployeeData(employees, teamList, employeeIds);
		log.info("exportEmployees: Successfully finished returning {} employees on exportEmployeeData",
				responseDtos.size());

		return new ResponseEntityDto(false, responseDtos);
	}

	@Override
	@Transactional
	public ResponseEntityDto getEmployeeById(Long employeeId) {
		User currentUser = userService.getCurrentUser();
		log.info("getEmployeeById: execution started by user: {}", currentUser.getUserId());

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
				.toList();

			managerEmployeeDto.setEmployeeProgressions(progressionResponseDtos);

			List<ManagingEmployeesResponseDto> managers = new ArrayList<>();

			employee.getEmployeeManagers().forEach(employeeManager -> {
				ManagingEmployeesResponseDto emp = new ManagingEmployeesResponseDto();
				emp.setEmployee(peopleMapper.employeeToManagerCoreDetailsDto(employeeManager.getEmployee()));
				emp.setManagerType(employeeManager.getManagerType());
				emp.setIsPrimaryManager(employeeManager.getIsPrimaryManager());

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

		return new ResponseEntityDto(false, employeeDetailedResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto addBulkEmployees(List<EmployeeBulkDto> employeeBulkDtoList) {
		List<EmployeeBulkDto> validEmployeeBulkDtoList = getValidEmployeeBulkDtoList(employeeBulkDtoList);
		User currentUser = userService.getCurrentUser();
		log.info("addEmployeeBulk: execution started by user: {}", currentUser.getUserId());

		ExecutorService executorService = Executors.newFixedThreadPool(6);
		List<EmployeeBulkResponseDto> results = Collections.synchronizedList(new ArrayList<>());
		AtomicReference<ResponseEntityDto> outValues = new AtomicReference<>(new ResponseEntityDto());

		List<CompletableFuture<Void>> tasks = createEmployeeTasks(validEmployeeBulkDtoList, executorService, results);
		waitForTaskCompletion(tasks, executorService);

		asyncEmailServiceImpl.sendEmailsInBackground(results);

		generateBulkErrorResponse(outValues, employeeBulkDtoList.size(), results);
		List<EmployeeBulkDto> overflowedEmployeeBulkDtoList = getOverFlowedEmployeeBulkDtoList(employeeBulkDtoList,
				validEmployeeBulkDtoList);

		List<EmployeeBulkResponseDto> totalResults = getTotalResultList(results, overflowedEmployeeBulkDtoList);

		int successCount = generateBulkErrorResponse(outValues, employeeBulkDtoList.size(), totalResults);
		updateSubscriptionQuantity(successCount, true);

		addNewBulkUploadedEmployeeTimeLineRecords(totalResults);

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
		log.info("getEmployeeByIdOrEmail: execution started");

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

		log.info("getEmployeeByIdOrEmail: execution ended");
		return new ResponseEntityDto(false, employeeDataValidationResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto terminateUser(Long userId) {
		log.info("terminateUser: execution started");

		updateUserStatus(userId, AccountStatus.TERMINATED, false);

		log.info("terminateUser: execution ended");
		return new ResponseEntityDto(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_SUCCESS_EMPLOYEE_TERMINATED),
				false);
	}

	@Override
	@Transactional
	public ResponseEntityDto deleteUser(Long userId) {
		log.info("deleteUser: execution started");

		updateUserStatus(userId, AccountStatus.DELETED, true);
		log.info("deleteUser: execution ended");

		return new ResponseEntityDto(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_SUCCESS_EMPLOYEE_DELETED),
				false);
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
			responseDto.setIsPrimaryManager(employeeManager.getIsPrimaryManager());
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

		if (currentUser.getEmployee().getEmployeeRole().getAttendanceRole() == Role.ATTENDANCE_MANAGER
				|| currentUser.getEmployee().getEmployeeRole().getLeaveRole() == Role.LEAVE_MANAGER) {
			List<Team> allTeams = teamDao.findTeamsByName(keyword);
			List<EmployeeTeam> employeeTeams = employeeTeamDao.findEmployeeTeamsByEmployee(currentUser.getEmployee());
			List<EmployeeManager> employeeManagers = employeeManagerDao.findByManager(currentUser.getEmployee());

			List<Team> supervisedTeams = allTeams.stream()
				.filter(team -> employeeTeams.stream()
					.anyMatch(et -> et.getTeam().getTeamId().equals(team.getTeamId()) && et.getIsSupervisor()))
				.toList();

			List<Employee> allEmployees = employeeDao.findEmployeeByName(keyword);

			Set<Long> managedEmployeeIds = employeeManagers.stream()
				.filter(em -> em.getManagerType() == ManagerType.PRIMARY
						|| em.getManagerType() == ManagerType.SECONDARY)
				.map(em -> em.getEmployee().getEmployeeId())
				.collect(Collectors.toSet());

			Set<Long> supervisedEmployeeIds = new HashSet<>();
			boolean isSupervisor = employeeTeams.stream().anyMatch(EmployeeTeam::getIsSupervisor);

			if (isSupervisor) {
				Set<Team> supervisedTeamIds = employeeTeams.stream()
					.filter(EmployeeTeam::getIsSupervisor)
					.map(EmployeeTeam::getTeam)
					.collect(Collectors.toSet());

				List<EmployeeTeam> teamMembers = employeeTeamDao.findByTeamIn(supervisedTeamIds);
				supervisedEmployeeIds = teamMembers.stream()
					.map(et -> et.getEmployee().getEmployeeId())
					.collect(Collectors.toSet());
			}

			Set<Long> finalSupervisedEmployeeIds = supervisedEmployeeIds;
			List<Employee> filteredEmployees = allEmployees.stream()
				.filter(employee -> managedEmployeeIds.contains(employee.getEmployeeId())
						|| finalSupervisedEmployeeIds.contains(employee.getEmployeeId()))
				.toList();

			AnalyticsSearchResponseDto analyticsSearchResponseDto = new AnalyticsSearchResponseDto(
					peopleMapper.employeeListToEmployeeSummarizedResponseDto(filteredEmployees),
					peopleMapper.teamToTeamDetailResponseDto(supervisedTeams));

			log.info("searchEmployeesAndTeamsByKeyword: execution ended by user: {} to search users by the keyword {}",
					currentUser.getUserId(), keyword);
			return new ResponseEntityDto(false, analyticsSearchResponseDto);
		}

		List<Team> teams = teamDao.findTeamsByName(keyword);
		List<Employee> employees = employeeDao.findEmployeeByName(keyword);

		AnalyticsSearchResponseDto analyticsSearchResponseDto = new AnalyticsSearchResponseDto(
				peopleMapper.employeeListToEmployeeSummarizedResponseDto(employees),
				peopleMapper.teamToTeamDetailResponseDto(teams));

		log.info("searchEmployeesAndTeamsByKeyword: execution ended by user: {} to search users by the keyword {}",
				currentUser.getUserId(), keyword);
		return new ResponseEntityDto(false, analyticsSearchResponseDto);
	}

	@Override
	public ResponseEntityDto isPrimarySecondaryOrTeamSupervisor(Long employeeId) {
		User currentUser = userService.getCurrentUser();

		Optional<Employee> employeeOptional = employeeDao.findById(employeeId);
		if (employeeOptional.isEmpty()) {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND);
		}

		List<EmployeeTeam> currentEmployeeTeams = employeeTeamDao
			.findEmployeeTeamsByEmployee(currentUser.getEmployee());
		List<EmployeeTeam> employeeTeams = employeeTeamDao.findEmployeeTeamsByEmployee(employeeOptional.get());

		PrimarySecondaryOrTeamSupervisorResponseDto primarySecondaryOrTeamSupervisor = employeeDao
			.isPrimarySecondaryOrTeamSupervisor(employeeOptional.get(), currentUser.getEmployee());

		boolean isTeamSupervisor = currentEmployeeTeams.stream()
			.anyMatch(currentTeam -> employeeTeams.stream()
				.anyMatch(empTeam -> currentTeam.getTeam().equals(empTeam.getTeam()) && currentTeam.getIsSupervisor()));

		primarySecondaryOrTeamSupervisor.setIsTeamSupervisor(isTeamSupervisor);
		return new ResponseEntityDto(false, primarySecondaryOrTeamSupervisor);
	}

	public List<EmployeeAllDataExportResponseDto> exportAllEmployeeData(List<Employee> employees,
			List<EmployeeTeamDto> teamList, List<Long> employeeIds) {
		List<EmployeeManagerDto> employeeManagerDtos = employeeDao.findManagersByEmployeeIds(employeeIds);
		List<EmployeeAllDataExportResponseDto> responseDtos = new ArrayList<>();

		for (Employee employee : employees) {
			EmployeeAllDataExportResponseDto responseDto = peopleMapper
				.employeeToEmployeeAllDataExportResponseDto(employee);
			responseDto.setJobFamily(peopleMapper.jobFamilyToJobFamilyDto(employee.getJobFamily()));
			responseDto.setJobTitle(peopleMapper.jobTitleToJobTitleDto(employee.getJobTitle()));
			responseDto.setEmployeePersonalInfoDto(
					peopleMapper.employeePersonalInfoToEmployeePersonalInfoDto(employee.getPersonalInfo()));
			responseDto.setEmployeeEmergencyDto(
					peopleMapper.employeeEmergencyToemployeeEmergencyDTo(employee.getEmployeeEmergencies()));

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
			if (employeeBulkDto.getEmployeeProgression().getEmploymentType() != null) {
				employee.setEmploymentType(employeeBulkDto.getEmployeeProgression().getEmploymentType());
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

	protected EmployeeBulkResponseDto createErrorResponse(EmployeeBulkDto employeeBulkDto, String message) {
		EmployeeBulkResponseDto bulkResponseDto = new EmployeeBulkResponseDto();
		bulkResponseDto.setEmail(employeeBulkDto.getWorkEmail() != null ? employeeBulkDto.getWorkEmail()
				: employeeBulkDto.getPersonalEmail());
		bulkResponseDto.setStatus(BulkItemStatus.ERROR);
		bulkResponseDto.setMessage(message);
		return bulkResponseDto;
	}

	protected List<EmployeeBulkDto> getValidEmployeeBulkDtoList(List<EmployeeBulkDto> employeeBulkDtoList) {
		return employeeBulkDtoList;
	}

	protected List<EmployeeBulkResponseDto> getTotalResultList(List<EmployeeBulkResponseDto> results,
			List<EmployeeBulkDto> overflowedEmployeeBulkDtoList) {
		if (!overflowedEmployeeBulkDtoList.isEmpty())
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_BULK_LIMIT_EXCEEDED);

		return results;
	}

	protected void updateSubscriptionQuantity(long quantity, boolean isIncrement) {
		log.info("updateSubscriptionQuantity: PRO feature {}, {}", quantity, isIncrement);
	}

	/**
	 * Validate the current user count with user limit. This method is only available for
	 * Pro tenants.
	 * @return eligibility for a new user upload.
	 */
	protected boolean checkUserCountExceeded() {
		return false;
	}

	/**
	 * Retrieves a deep copy of the given employee. This method is only available for Pro
	 * tenants.
	 * @param currentEmployee The employee to create a deep copy from.
	 * @return A deep copy of the given employee as a CurrentEmployeeDto.
	 */
	protected CurrentEmployeeDto getEmployeeDeepCopy(Employee currentEmployee) {
		return null;
	}

	/**
	 * Adds a new timeline record when a new employee is created. This feature is
	 * available only for Pro tenants.
	 * @param savedEmployee The newly saved employee entity.
	 * @param employeeDetailsDto The details of the newly created employee.
	 */
	protected void addNewEmployeeTimeLineRecords(Employee savedEmployee, CreateEmployeeRequestDto employeeDetailsDto) {
		// This feature is available only for Pro tenants.
	}

	/**
	 * Adds a new timeline record for employees who are added via quick upload. This
	 * feature is available only for Pro tenants.
	 * @param savedEmployee The employee added through quick upload.
	 * @param employeeQuickAddDto The quick-add details of the employee.
	 */
	protected void addNewQuickUploadedEmployeeTimeLineRecords(Employee savedEmployee,
			EmployeeQuickAddDto employeeQuickAddDto) {
		// This feature is available only for Pro tenants.
	}

	/**
	 * Adds new timeline records for employees who are added via bulk upload. This feature
	 * is available only for Pro tenants.
	 * @param results The employees added through bulk upload.
	 */
	protected void addNewBulkUploadedEmployeeTimeLineRecords(List<EmployeeBulkResponseDto> results) {
		// This feature is available only for Pro tenants.
	}

	/**
	 * Adds a new timeline record when an existing employee's details are updated. This
	 * feature is available only for Pro tenants.
	 * @param currentEmployee The current state of the employee before the update.
	 * @param employeeUpdateDto The updated details of the employee.
	 */
	protected void addUpdatedEmployeeTimeLineRecords(CurrentEmployeeDto currentEmployee,
			EmployeeUpdateDto employeeUpdateDto) {
		// This feature is available only for Pro tenants.
	}

	private void setEmployeeTeams(List<TeamEmployeeResponseDto> teams, Employee employee) {
		employee.getEmployeeTeams().forEach(employeeTeam -> {
			TeamEmployeeResponseDto teamEmployeeResponseDto = new TeamEmployeeResponseDto();
			TeamDetailResponseDto team = new TeamDetailResponseDto();
			team.setIsSupervisor(employeeTeam.getIsSupervisor());
			team.setTeamName(employeeTeam.getTeam().getTeamName());
			team.setTeamId(employeeTeam.getTeam().getTeamId());
			teamEmployeeResponseDto.setTeam(team);
			teams.add(teamEmployeeResponseDto);
		});
	}

	private void handleGeneralException(EmployeeBulkDto employeeBulkDto, Exception e,
			List<EmployeeBulkResponseDto> results) {
		log.warn("addEmployeeBulk: exception occurred when saving : {}", e.getMessage());
		EmployeeBulkResponseDto bulkResponseDto = createErrorResponse(employeeBulkDto, e.getMessage());
		results.add(bulkResponseDto);
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

	private int generateBulkErrorResponse(AtomicReference<ResponseEntityDto> outValues, int totalSize,
			List<EmployeeBulkResponseDto> results) {
		EmployeeBulkErrorResponseDto errorResponseDto = new EmployeeBulkErrorResponseDto();

		List<EmployeeBulkResponseDto> errorResults = results.stream()
			.filter(responseDto -> responseDto.getStatus() == BulkItemStatus.ERROR)
			.toList();

		int successCount = totalSize - errorResults.size();
		errorResponseDto.setBulkStatusSummary(new BulkStatusSummary(successCount, errorResults.size()));
		errorResponseDto.setBulkRecordErrorLogs(errorResults);
		outValues.set(new ResponseEntityDto(false, errorResponseDto));

		return successCount;
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
		employee.setEmployeeManagers(managers);

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

		UserSettings userSettings = createNotificationSettingsForBulkUser(user);
		user.setSettings(userSettings);

		userDao.save(user);
		applicationEventPublisher.publishEvent(new UserCreatedEvent(this, user));

		rolesService.saveEmployeeRoles(employee);
		saveEmployeeProgression(employee, employeeBulkDto);

		if (employeeBulkDto.getTeams() != null && !employeeBulkDto.getTeams().isEmpty()) {
			saveEmployeeTeams(employee, employeeBulkDto);
		}

		if (employeeBulkDto.getEmployeePeriod() != null) {
			saveEmployeePeriod(employee, employeeBulkDto.getEmployeePeriod());
		}
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
				employeeProgression.setEmploymentType(EmploymentType.valueOf(employeeBulkDto.getEmployeeType()));
			}

			employeeProgression.setEmployee(employee);
			employeeProgressions.add(employeeProgression);
			employee.setEmployeeProgressions(employeeProgressions);

			employeeDao.save(employee);
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

	private UserSettings createNotificationSettingsForBulkUser(User user) {
		log.info("createNotificationSettingsForBulkUser: execution started");
		UserSettings userSettings = new UserSettings();

		EmployeeRole employeeRole = rolesService.setupBulkEmployeeRoles(user.getEmployee());
		ObjectNode notificationsObjectNode = mapper.createObjectNode();

		boolean isLeaveRequestNotificationsEnabled = true;
		boolean isTimeEntryNotificationsEnabled = true;
		boolean isNudgeNotificationsEnabled = employeeRole.getIsSuperAdmin()
				|| employeeRole.getLeaveRole() == Role.LEAVE_MANAGER || employeeRole.getLeaveRole() == Role.LEAVE_ADMIN;

		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST.getKey(),
				isLeaveRequestNotificationsEnabled);
		notificationsObjectNode.put(NotificationSettingsType.TIME_ENTRY.getKey(), isTimeEntryNotificationsEnabled);
		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST_NUDGE.getKey(), isNudgeNotificationsEnabled);

		userSettings.setNotifications(notificationsObjectNode);
		userSettings.setUser(user);

		log.info("createNotificationSettingsForBulkUser: execution ended");
		return userSettings;
	}

	private UserSettings createNotificationSettings(EmployeeSystemPermissionsDto roleRequestDto, User user) {
		log.info("createNotificationSettings: execution started");
		UserSettings userSettings = new UserSettings();

		ObjectNode notificationsObjectNode = mapper.createObjectNode();

		boolean isLeaveRequestNotificationsEnabled = true;
		boolean isTimeEntryNotificationsEnabled = true;
		boolean isNudgeNotificationsEnabled = roleRequestDto.getIsSuperAdmin()
				|| roleRequestDto.getLeaveRole() == Role.LEAVE_MANAGER
				|| roleRequestDto.getLeaveRole() == Role.LEAVE_ADMIN;

		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST.getKey(),
				isLeaveRequestNotificationsEnabled);
		notificationsObjectNode.put(NotificationSettingsType.TIME_ENTRY.getKey(), isTimeEntryNotificationsEnabled);
		notificationsObjectNode.put(NotificationSettingsType.LEAVE_REQUEST_NUDGE.getKey(), isNudgeNotificationsEnabled);

		userSettings.setNotifications(notificationsObjectNode);
		userSettings.setUser(user);

		log.info("createNotificationSettings: execution ended");
		return userSettings;
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
	}

	private Employee getManager(Long managerId) {
		return employeeDao.findEmployeeByEmployeeIdAndUserActiveNot(managerId, false)
			.orElseThrow(() -> new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_MANAGER_NOT_FOUND));
	}

	private EmployeeManager createEmployeeManager(Employee manager, Employee employee, boolean directManager) {
		EmployeeManager employeeManager = new EmployeeManager();
		employeeManager.setManager(manager);
		employeeManager.setEmployee(employee);
		employeeManager.setIsPrimaryManager(directManager);
		employeeManager.setManagerType(directManager ? ManagerType.PRIMARY : ManagerType.SECONDARY);
		return employeeManager;
	}

	private void saveEmployeePeriod(Employee finalEmployee, ProbationPeriodDto probationPeriodDto) {
		EmployeePeriod employeePeriod = new EmployeePeriod();
		employeePeriod.setEmployee(finalEmployee);
		employeePeriod.setStartDate(probationPeriodDto.getStartDate());
		employeePeriod.setEndDate(probationPeriodDto.getEndDate());
		employeePeriod.setIsActive(true);
		employeePeriodDao.save(employeePeriod);
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

	private void validateCreateEmployeeRequestEmploymentDetails(EmployeeEmploymentDetailsDto employmentDetailsDto) {
		if (employmentDetailsDto != null) {
			if (employmentDetailsDto.getEmploymentDetails() != null) {
				if (employmentDetailsDto.getEmploymentDetails().getEmployeeNumber() != null
						&& !employmentDetailsDto.getEmploymentDetails().getEmployeeNumber().isEmpty()) {
					Validations.validateEmployeeNumber(employmentDetailsDto.getEmploymentDetails().getEmployeeNumber());
				}
				if (employmentDetailsDto.getEmploymentDetails().getTeamIds() != null
						&& employmentDetailsDto.getEmploymentDetails().getTeamIds().length > 0) {
					Set<Long> activeTeamIdSet = teamDao.findAllByIsActive(true)
						.stream()
						.map(Team::getTeamId)
						.collect(Collectors.toSet());

					Set<Long> invalidTeamIds = Arrays.stream(employmentDetailsDto.getEmploymentDetails().getTeamIds())
						.filter(id -> !activeTeamIdSet.contains(id))
						.collect(Collectors.toSet());

					if (!invalidTeamIds.isEmpty()) {
						throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_INVALID_TEAM_IDS,
								List.of(String.valueOf(invalidTeamIds)));
					}
				}

				if (employmentDetailsDto.getEmploymentDetails().getPrimarySupervisor() != null) {
					if (employmentDetailsDto.getEmploymentDetails().getPrimarySupervisor().getEmployeeId() == null
							|| employmentDetailsDto.getEmploymentDetails()
								.getPrimarySupervisor()
								.getEmployeeId() <= 0) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PRIMARY_SUPERVISOR_EMPLOYEE_ID_REQUIRED);
					}

					Optional<Employee> primarySupervisor = employeeDao
						.findById(employmentDetailsDto.getEmploymentDetails().getPrimarySupervisor().getEmployeeId());
					if (primarySupervisor.isEmpty()) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PRIMARY_SUPERVISOR_EMPLOYEE_NOT_FOUND);
					}

					EmployeeRole role = primarySupervisor.get().getEmployeeRole();
					if (role.getPeopleRole() == Role.PEOPLE_EMPLOYEE && role.getLeaveRole() == Role.LEAVE_EMPLOYEE
							&& role.getAttendanceRole() == Role.ATTENDANCE_EMPLOYEE) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PRIMARY_SUPERVISOR_MANAGER_TYPE_REQUIRED);
					}

					if ((employmentDetailsDto.getEmploymentDetails().getPrimarySupervisor() == null
							|| employmentDetailsDto.getEmploymentDetails()
								.getPrimarySupervisor()
								.getEmployeeId() == null)
							&& employmentDetailsDto.getEmploymentDetails().getSecondarySupervisor() != null
							&& employmentDetailsDto.getEmploymentDetails()
								.getSecondarySupervisor()
								.getEmployeeId() != null) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PRIMARY_SUPERVISOR_MANAGER_TYPE_REQUIRED);
					}

					if (employmentDetailsDto.getEmploymentDetails().getPrimarySupervisor() != null
							&& employmentDetailsDto.getEmploymentDetails()
								.getPrimarySupervisor()
								.getEmployeeId() != null
							&& employmentDetailsDto.getEmploymentDetails().getSecondarySupervisor() != null
							&& employmentDetailsDto.getEmploymentDetails()
								.getSecondarySupervisor()
								.getEmployeeId() != null) {
						if (Objects.equals(
								employmentDetailsDto.getEmploymentDetails().getPrimarySupervisor().getEmployeeId(),
								employmentDetailsDto.getEmploymentDetails().getSecondarySupervisor().getEmployeeId())) {
							throw new ValidationException(
									PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PRIMARY_SECONDARY_SUPERVISOR_SAME);
						}
					}

					if (employmentDetailsDto.getEmploymentDetails().getSecondarySupervisor() != null
							&& employmentDetailsDto.getEmploymentDetails()
								.getSecondarySupervisor()
								.getEmployeeId() != null) {
						Optional<Employee> secondarySupervisor = employeeDao.findById(
								employmentDetailsDto.getEmploymentDetails().getSecondarySupervisor().getEmployeeId());
						if (secondarySupervisor.isEmpty()) {
							throw new ValidationException(
									PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_SECONDARY_SUPERVISOR_EMPLOYEE_NOT_FOUND);
						}
					}

					if (employmentDetailsDto.getEmploymentDetails().getJoinedDate() != null
							&& employmentDetailsDto.getEmploymentDetails().getJoinedDate().isAfter(LocalDate.now())) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR__VALIDATION_EMPLOYMENT_JOINED_DATE_FUTURE_DATE);
					}

					if (employmentDetailsDto.getEmploymentDetails().getJoinedDate() != null
							&& employmentDetailsDto.getEmploymentDetails().getJoinedDate().isAfter(LocalDate.now())) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR__VALIDATION_EMPLOYMENT_JOINED_DATE_FUTURE_DATE);
					}

					LocalDate joinedDate = employmentDetailsDto.getEmploymentDetails().getJoinedDate();
					LocalDate probationStartDate = employmentDetailsDto.getEmploymentDetails().getProbationStartDate();
					LocalDate probationEndDate = employmentDetailsDto.getEmploymentDetails().getProbationEndDate();

					if (joinedDate != null && probationStartDate != null && probationStartDate.isBefore(joinedDate)) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PROBATION_START_DATE_BEFORE_JOINED_DATE);
					}

					if (probationStartDate != null && probationEndDate != null
							&& probationEndDate.isBefore(probationStartDate)) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PROBATION_END_DATE_BEFORE_START_DATE);
					}

					if (joinedDate != null && probationEndDate != null && probationEndDate.isBefore(joinedDate)) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PROBATION_END_DATE_BEFORE_JOINED_DATE);
					}
				}
			}

			if (employmentDetailsDto.getCareerProgression() != null
					&& !employmentDetailsDto.getCareerProgression().isEmpty()) {
				employmentDetailsDto.getCareerProgression().forEach(progression -> {
					if (progression.getEmploymentType() == null) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_EMPLOYMENT_TYPE_REQUIRED);
					}

					if (progression.getJobFamilyId() == null) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_CAREER_PROGRESSION_JOB_FAMILY_REQUIRED);
					}

					if (progression.getJobTitleId() == null) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_CAREER_PROGRESSION_JOB_TITLE_REQUIRED);
					}

					if (progression.getStartDate() == null) {
						throw new ValidationException(
								PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_CAREER_PROGRESSION_START_DATE_REQUIRED);
					}

					if (Boolean.FALSE.equals(progression.getIsCurrentEmployment())) {
						if (progression.getEndDate() == null) {
							throw new ValidationException(
									PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_CAREER_PROGRESSION_END_DATE_REQUIRED);
						}

						if (progression.getEndDate().isBefore(progression.getStartDate())) {
							throw new ValidationException(
									PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_CAREER_END_DATE_BEFORE_START_DATE);
						}
					}

					if (Boolean.TRUE.equals(progression.getIsCurrentEmployment())) {
						if (progression.getEndDate() != null) {
							throw new ValidationException(
									PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_CAREER_PROGRESSION_END_DATE_NOT_REQUIRED_FOR_CURRENT_EMPLOYMENT_TRUE);
						}
					}
				});
			}
		}
	}

	private void validateCreateEmployeeRequestRequiredFields(CreateEmployeeRequestDto createEmployeeRequestDto) {
		if (checkUserCountExceeded()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_LIMIT_EXCEEDED);
		}

		Optional<User> existingUser = userDao
			.findByEmail(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmail());
		if (existingUser.isPresent()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_USER_EMAIL_ALREADY_EXIST);
		}

		if (createEmployeeRequestDto.getPersonal().getGeneral().getFirstName() == null
				|| createEmployeeRequestDto.getPersonal().getGeneral().getFirstName().isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_FIRST_NAME_REQUIRED);
		}

		Validations.validateFirstName(createEmployeeRequestDto.getPersonal().getGeneral().getFirstName());

		if (createEmployeeRequestDto.getPersonal().getGeneral().getLastName() == null
				|| createEmployeeRequestDto.getPersonal().getGeneral().getLastName().isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_LAST_NAME_REQUIRED);
		}

		Validations.validateLastName(createEmployeeRequestDto.getPersonal().getGeneral().getLastName());

		if (createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmail() == null
				|| createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmail().isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_EMAIL_REQUIRED);
		}

		Validations.validateWorkEmail(createEmployeeRequestDto.getEmployment().getEmploymentDetails().getEmail());

		rolesService.validateRoles(createEmployeeRequestDto.getSystemPermissions());
	}

	private void validateCreateEmployeeRequestPersonalDetails(EmployeePersonalDetailsDto employeePersonalDetailsDto) {
		if (employeePersonalDetailsDto != null) {
			if (employeePersonalDetailsDto.getGeneral() != null) {
				if (employeePersonalDetailsDto.getGeneral().getMiddleName() != null
						&& !employeePersonalDetailsDto.getGeneral().getMiddleName().isEmpty()) {
					Validations.validateMiddleName(employeePersonalDetailsDto.getGeneral().getMiddleName());
				}

				if (employeePersonalDetailsDto.getGeneral().getNin() != null
						&& !employeePersonalDetailsDto.getGeneral().getNin().isEmpty()) {
					Validations.validateNIN(employeePersonalDetailsDto.getGeneral().getNin());
				}

				if (employeePersonalDetailsDto.getGeneral().getDateOfBirth() != null) {
					if (employeePersonalDetailsDto.getGeneral().getDateOfBirth().isAfter(LocalDate.now())) {
						throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_DOB_FUTURE_DATE);
					}
				}
			}

			if (employeePersonalDetailsDto.getContact() != null) {
				if (employeePersonalDetailsDto.getContact().getPersonalEmail() != null
						&& !employeePersonalDetailsDto.getContact().getPersonalEmail().isEmpty()) {
					Validations.validatePersonalEmail(employeePersonalDetailsDto.getContact().getPersonalEmail());
				}

				if (employeePersonalDetailsDto.getContact().getContactNo() != null
						&& !employeePersonalDetailsDto.getContact().getContactNo().isEmpty()) {
					Validations.validateEmployeeContactNo(employeePersonalDetailsDto.getContact().getContactNo());
				}

				if (employeePersonalDetailsDto.getContact().getAddressLine1() != null
						&& !employeePersonalDetailsDto.getContact().getAddressLine1().isEmpty()) {
					Validations.validateAddressLine1(employeePersonalDetailsDto.getContact().getAddressLine1());
				}

				if (employeePersonalDetailsDto.getContact().getAddressLine2() != null
						&& !employeePersonalDetailsDto.getContact().getAddressLine2().isEmpty()) {
					Validations.validateAddressLine2(employeePersonalDetailsDto.getContact().getAddressLine2());
				}

				if (employeePersonalDetailsDto.getContact().getCountry() != null
						&& !employeePersonalDetailsDto.getContact().getCountry().isEmpty()) {
					Validations.validateCountry(employeePersonalDetailsDto.getContact().getCountry());
				}

				if (employeePersonalDetailsDto.getContact().getCity() != null
						&& !employeePersonalDetailsDto.getContact().getCity().isEmpty()) {
					Validations.validateCity(employeePersonalDetailsDto.getContact().getCity());
				}

				if (employeePersonalDetailsDto.getContact().getState() != null
						&& !employeePersonalDetailsDto.getContact().getState().isEmpty()) {
					Validations.validateState(employeePersonalDetailsDto.getContact().getState());
				}

				if (employeePersonalDetailsDto.getContact().getPostalCode() != null
						&& !employeePersonalDetailsDto.getContact().getPostalCode().isEmpty()) {
					Validations.validatePostalCode(employeePersonalDetailsDto.getContact().getPostalCode());
				}
			}

			if (employeePersonalDetailsDto.getFamily() != null && !employeePersonalDetailsDto.getFamily().isEmpty()) {
				employeePersonalDetailsDto.getFamily().forEach(familyDto -> {
					if (familyDto.getFirstName() != null && !familyDto.getFirstName().isEmpty()) {
						Validations.validateFamilyFirstName(familyDto.getFirstName());
					}
					if (familyDto.getLastName() != null && !familyDto.getLastName().isEmpty()) {
						Validations.validateFamilyLastName(familyDto.getLastName());
					}
					if (familyDto.getParentName() != null && !familyDto.getParentName().isEmpty()) {
						Validations.validateFamilyParentName(familyDto.getParentName());
					}
					if (familyDto.getDateOfBirth() != null) {
						if (familyDto.getDateOfBirth().isAfter(LocalDate.now())) {
							throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_FAMILY_DOB_FUTURE_DATE);
						}
					}
				});
			}

			if (employeePersonalDetailsDto.getEducational() != null
					&& !employeePersonalDetailsDto.getEducational().isEmpty()) {
				employeePersonalDetailsDto.getEducational().forEach(educationDto -> {
					if (educationDto.getStartDate() != null && educationDto.getEndDate() != null) {
						if (educationDto.getStartDate().isAfter(educationDto.getEndDate())) {
							throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_EDUCATION_START_END_DATE);
						}
					}
				});
			}
		}
	}

	private void validateQuickAddEmployeeRequestRequiredFields(EmployeeQuickAddDto employeeQuickAddDto) {
		if (checkUserCountExceeded()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_LIMIT_EXCEEDED);
		}

		Optional<User> existingUser = userDao.findByEmail(employeeQuickAddDto.getEmail());
		if (existingUser.isPresent()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_USER_EMAIL_ALREADY_EXIST);
		}

		if (employeeQuickAddDto.getFirstName() == null || employeeQuickAddDto.getFirstName().isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_FIRST_NAME_REQUIRED);
		}

		Validations.validateFirstName(employeeQuickAddDto.getFirstName());

		if (employeeQuickAddDto.getLastName() == null || employeeQuickAddDto.getLastName().isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_LAST_NAME_REQUIRED);
		}

		Validations.validateLastName(employeeQuickAddDto.getLastName());

		if (employeeQuickAddDto.getEmail() == null || employeeQuickAddDto.getEmail().isEmpty()) {
			throw new ValidationException(PeopleMessageConstant.PEOPLE_ERROR_EMAIL_REQUIRED);
		}

		Validations.validateWorkEmail(employeeQuickAddDto.getEmail());

		rolesService.validateRoles(employeeQuickAddDto.getUserRoles());
	}

	private EmployeeEmergency createEmployeeEmergency(EmployeeEmergencyContactDetailsDto contactDto, boolean isPrimary,
			Employee employee) {
		EmployeeEmergency contact = new EmployeeEmergency();
		contact.setName(contactDto.getName());
		contact.setEmergencyRelationship(contactDto.getRelationship());
		contact.setContactNo(contactDto.getCountryCode() + contactDto.getContactNo());
		contact.setIsPrimary(isPrimary);
		contact.setEmployee(employee);
		return contact;
	}

	private EmployeeProgressionResponseDto mapToEmployeeProgressionResponseDto(EmployeeProgression progression) {
		EmployeeProgressionResponseDto responseDto = new EmployeeProgressionResponseDto();

		responseDto.setProgressionId(progression.getProgressionId());
		responseDto.setEmploymentType(progression.getEmploymentType());
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

	private List<EmployeeBulkDto> getOverFlowedEmployeeBulkDtoList(List<EmployeeBulkDto> employeeBulkDtoList,
			List<EmployeeBulkDto> validList) {
		return employeeBulkDtoList.stream().filter(e -> !validList.contains(e)).toList();
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

	private void updateUserStatus(Long userId, AccountStatus status, boolean isDelete) {
		log.info("updateUserStatus: execution started");

		User user = userDao.findById(userId)
			.orElseThrow(() -> new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND));
		Employee employee = user.getEmployee();

		if (!Boolean.TRUE.equals(user.getIsActive())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_ACCOUNT_DEACTIVATED);
		}

		if (!teamDao.findTeamsManagedByUser(user.getUserId(), true).isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_TEAM_EMPLOYEE_SUPERVISING_TEAMS);
		}

		if (employeeDao.countEmployeesByManagerId(user.getUserId()) > 0) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EMPLOYEE_SUPERVISING_EMPLOYEES);
		}

		List<EmployeeTeam> employeeTeams = employeeTeamDao.findEmployeeTeamsByEmployee(employee);
		employeeTeamDao.deleteAll(employeeTeams);
		employee.setEmployeeTeams(null);

		employee.setJobTitle(null);
		employee.setJobFamily(null);
		employee.setAccountStatus(status);
		employee.setTerminationDate(DateTimeUtils.getCurrentUtcDate());

		user.setIsActive(false);

		if (isDelete) {
			user.setEmail(PeopleConstants.DELETED_PREFIX + user.getEmail());
		}
		else {
			peopleEmailService.sendUserTerminationEmail(user);
		}

		userDao.save(user);
		applicationEventPublisher.publishEvent(new UserDeactivatedEvent(this, user));

		updateSubscriptionQuantity(1L, false);
		userVersionService.upgradeUserVersion(user.getUserId(), VersionType.MAJOR);
	}

}
