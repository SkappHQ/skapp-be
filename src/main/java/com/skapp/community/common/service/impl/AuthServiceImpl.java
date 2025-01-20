package com.skapp.community.common.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.skapp.community.common.component.ProfileActivator;
import com.skapp.community.common.constant.CommonConstants;
import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.mapper.CommonMapper;
import com.skapp.community.common.model.OrganizationConfig;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.ReInvitationSkippedCountDto;
import com.skapp.community.common.payload.request.ChangePasswordRequestDto;
import com.skapp.community.common.payload.request.ForgotPasswordRequestDto;
import com.skapp.community.common.payload.request.ReInvitationRequestDto;
import com.skapp.community.common.payload.request.RefreshTokenRequestDto;
import com.skapp.community.common.payload.request.ResetPasswordRequestDto;
import com.skapp.community.common.payload.request.SignInRequestDto;
import com.skapp.community.common.payload.request.SuperAdminSignUpRequestDto;
import com.skapp.community.common.payload.response.AccessTokenResponseDto;
import com.skapp.community.common.payload.response.BulkResponseDto;
import com.skapp.community.common.payload.response.BulkStatusSummaryDto;
import com.skapp.community.common.payload.response.EmployeeSignInResponseDto;
import com.skapp.community.common.payload.response.ErrorLogDto;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.payload.response.SharePasswordResponseDto;
import com.skapp.community.common.payload.response.SignInResponseDto;
import com.skapp.community.common.repository.OrganizationConfigDao;
import com.skapp.community.common.repository.UserDao;
import com.skapp.community.common.service.AuthService;
import com.skapp.community.common.service.BulkContextService;
import com.skapp.community.common.service.EncryptionDecryptionService;
import com.skapp.community.common.service.JwtService;
import com.skapp.community.common.service.UserService;
import com.skapp.community.common.type.BulkItemStatus;
import com.skapp.community.common.type.LoginMethod;
import com.skapp.community.common.type.OrganizationConfigType;
import com.skapp.community.common.util.CommonModuleUtils;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.common.util.Validation;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.payload.response.EmployeeCredentialsResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeResponseDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeRoleDao;
import com.skapp.community.peopleplanner.service.PeopleEmailService;
import com.skapp.community.peopleplanner.service.PeopleNotificationService;
import com.skapp.community.peopleplanner.service.RolesService;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.EmploymentAllocation;
import com.skapp.community.peopleplanner.util.Validations;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	@NonNull
	private final UserDao userDao;

	@NonNull
	private final UserDetailsService userDetailsService;

	@NonNull
	private final PeopleMapper peopleMapper;

	@NonNull
	private final EmployeeDao employeeDao;

	@NonNull
	private final JwtService jwtService;

	@NonNull
	private final AuthenticationManager authenticationManager;

	@NonNull
	private final PasswordEncoder passwordEncoder;

	@NonNull
	private final EmployeeRoleDao employeeRoleDao;

	@NonNull
	private final CommonMapper commonMapper;

	@NonNull
	private final UserService userService;

	@NonNull
	private final PeopleEmailService peopleEmailService;

	@NonNull
	private final PeopleNotificationService peopleNotificationService;

	@NonNull
	private final EncryptionDecryptionService encryptionDecryptionService;

	@NonNull
	private final ProfileActivator profileActivator;

	@NonNull
	private final PlatformTransactionManager transactionManager;

	@NonNull
	private final BulkContextService bulkContextService;

	private final MessageUtil messageUtil;

	@NonNull
	private final RolesService rolesService;

	private final OrganizationConfigDao organizationConfigDao;

	@Value("${encryptDecryptAlgorithm.secret}")
	private String encryptSecret;

	@Override
	public ResponseEntityDto signIn(SignInRequestDto signInRequestDto) {
		log.info("signIn: execution started");

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(), signInRequestDto.getPassword()));

		Optional<User> optionalUser = userDao.findByEmail(signInRequestDto.getEmail());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		User user = optionalUser.get();

		if (Boolean.FALSE.equals(user.getIsActive())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_ACCOUNT_DEACTIVATED);
		}

		if (user.getEmployee().getAccountStatus() == AccountStatus.PENDING && profileActivator.isEpProfile()
				&& employeeDao.countByAccountStatus(AccountStatus.ACTIVE) >= CommonConstants.EP_FREE_USER_LIMIT) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EXCEED_MAX_EMPLOYEE_COUNT);
		}

		Optional<Employee> employee = employeeDao.findById(user.getUserId());
		if (employee.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		Employee userEmployee = employee.get();

		if (userEmployee.getAccountStatus() == AccountStatus.PENDING) {
			userEmployee.setAccountStatus(AccountStatus.ACTIVE);
			employeeDao.save(userEmployee);
		}

		EmployeeSignInResponseDto employeeSignInResponseDto = peopleMapper
			.employeeToEmployeeSignInResponseDto(employee.get());

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String accessToken = jwtService.generateAccessToken(userDetails, user.getUserId());
		String refreshToken = jwtService.generateRefreshToken(userDetails);

		SignInResponseDto signInResponseDto = new SignInResponseDto();
		signInResponseDto.setAccessToken(accessToken);
		signInResponseDto.setRefreshToken(refreshToken);
		signInResponseDto.setEmployee(employeeSignInResponseDto);
		signInResponseDto.setIsPasswordChangedForTheFirstTime(user.getIsPasswordChangedForTheFirstTime());

		log.info("signIn: execution ended");
		return new ResponseEntityDto(false, signInResponseDto);
	}

	@Transactional
	@Override
	public ResponseEntityDto superAdminSignUp(SuperAdminSignUpRequestDto superAdminSignUpRequestDto) {
		log.info("superAdminSignUp: execution started");

		boolean isSuperAdminExists = isSuperAdminExists();
		if (isSuperAdminExists) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_SUPER_ADMIN_ALREADY_EXISTS);
		}

		Optional<User> optionalUser = userDao.findByEmail(superAdminSignUpRequestDto.getEmail());
		if (optionalUser.isPresent()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_ALREADY_EXISTS);
		}

		Validation.isValidFirstName(superAdminSignUpRequestDto.getFirstName());
		Validation.isValidLastName(superAdminSignUpRequestDto.getLastName());
		Validation.validateEmail(superAdminSignUpRequestDto.getEmail());
		Validation.isValidPassword(superAdminSignUpRequestDto.getPassword());

		User user = commonMapper.createSuperAdminRequestDtoToUser(superAdminSignUpRequestDto);
		user.setPassword(passwordEncoder.encode(superAdminSignUpRequestDto.getPassword()));
		user.setIsPasswordChangedForTheFirstTime(true);

		Employee employee = peopleMapper.createSuperAdminRequestDtoToEmployee(superAdminSignUpRequestDto);
		employee.setAccountStatus(AccountStatus.ACTIVE);
		employee.setEmploymentAllocation(EmploymentAllocation.FULL_TIME);
		user.setEmployee(employee);
		employee.setUser(user);

		userDao.save(user);

		rolesService.saveSuperAdminRoles(employee);
		employeeDao.save(employee);

		EmployeeSignInResponseDto employeeSignInResponseDto = peopleMapper
			.employeeToEmployeeSignInResponseDto(employee);

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String accessToken = jwtService.generateAccessToken(userDetails, user.getUserId());
		String refreshToken = jwtService.generateRefreshToken(userDetails);

		SignInResponseDto signInResponseDto = new SignInResponseDto();
		signInResponseDto.setAccessToken(accessToken);
		signInResponseDto.setRefreshToken(refreshToken);
		signInResponseDto.setEmployee(employeeSignInResponseDto);
		signInResponseDto.setIsPasswordChangedForTheFirstTime(true);

		log.info("superAdminSignUp: execution ended");
		return new ResponseEntityDto(false, signInResponseDto);
	}

	@Override
	public ResponseEntityDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequestDto) {
		log.info("refreshAccessToken: execution started");

		if (!jwtService.isRefreshToken(refreshTokenRequestDto.getRefreshToken())
				|| jwtService.isTokenExpired(refreshTokenRequestDto.getRefreshToken())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_INVALID_REFRESH_TOKEN);
		}

		String userEmail = jwtService.extractUserEmail(refreshTokenRequestDto.getRefreshToken());
		UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

		if (!jwtService.isTokenValid(refreshTokenRequestDto.getRefreshToken(), userDetails)) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_INVALID_REFRESH_TOKEN);
		}

		Optional<User> optionalUser = userDao.findByEmail(userEmail);
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		User user = optionalUser.get();

		EmployeeResponseDto employeeResponseDto = peopleMapper.employeeToEmployeeResponseDto(user.getEmployee());
		String accessToken = jwtService.generateAccessToken(userDetails, user.getUserId());

		AccessTokenResponseDto accessTokenResponseDto = new AccessTokenResponseDto();
		accessTokenResponseDto.setAccessToken(accessToken);
		accessTokenResponseDto.setEmployee(employeeResponseDto);

		log.info("refreshAccessToken: execution ended");
		return new ResponseEntityDto(false, accessTokenResponseDto);
	}

	@Override
	public ResponseEntityDto employeeResetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
		log.info("employeeResetPassword: execution started");

		User user = userService.getCurrentUser();
		if (user == null) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		if (Boolean.TRUE.equals(user.getIsPasswordChangedForTheFirstTime())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_ALREADY_PASSWORD_RESET);
		}

		String newPassword = resetPasswordRequestDto.getNewPassword();
		createNewPassword(newPassword, user);

		log.info("employeeResetPassword: execution ended");
		return new ResponseEntityDto(false, "User password reset successfully");
	}

	@Override
	public ResponseEntityDto sharePassword(Long userId) {
		log.info("sharePassword: execution started");

		Optional<User> optionalUser = userDao.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		User user = optionalUser.get();

		SharePasswordResponseDto sharePasswordResponseDto = new SharePasswordResponseDto();
		sharePasswordResponseDto.setUserId(user.getUserId());

		EmployeeCredentialsResponseDto employeeCredentialsResponseDto = new EmployeeCredentialsResponseDto();
		employeeCredentialsResponseDto.setEmail(user.getEmail());
		employeeCredentialsResponseDto
			.setTempPassword(encryptionDecryptionService.decrypt(user.getTempPassword(), encryptSecret));

		sharePasswordResponseDto.setEmployeeCredentials(employeeCredentialsResponseDto);
		sharePasswordResponseDto.setFirstName(user.getEmployee().getFirstName());
		sharePasswordResponseDto.setLastName(user.getEmployee().getLastName());

		log.info("sharePassword: execution ended");
		return new ResponseEntityDto(false, sharePasswordResponseDto);
	}

	@Override
	public ResponseEntityDto resetAndSharePassword(Long userId) {
		log.info("resetAndSharePassword: execution started");

		Optional<User> optionalUser = userDao.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}
		User user = optionalUser.get();

		String tempPassword = CommonModuleUtils.generateSecureRandomPassword();
		user.setTempPassword(encryptionDecryptionService.encrypt(tempPassword, encryptSecret));
		user.setPassword(passwordEncoder.encode(tempPassword));
		user.setIsPasswordChangedForTheFirstTime(true);
		User savedUser = userDao.save(user);

		SharePasswordResponseDto sharePasswordResponseDto = new SharePasswordResponseDto();
		sharePasswordResponseDto.setUserId(savedUser.getUserId());

		EmployeeCredentialsResponseDto employeeCredentialsResponseDto = new EmployeeCredentialsResponseDto();
		employeeCredentialsResponseDto.setEmail(user.getEmail());
		employeeCredentialsResponseDto.setTempPassword(tempPassword);

		sharePasswordResponseDto.setEmployeeCredentials(employeeCredentialsResponseDto);
		sharePasswordResponseDto.setFirstName(user.getEmployee().getFirstName());
		sharePasswordResponseDto.setLastName(user.getEmployee().getLastName());

		log.info("resetAndSharePassword: execution ended");
		return new ResponseEntityDto(false, sharePasswordResponseDto);
	}

	@Override
	public ResponseEntityDto sendReInvitation(ReInvitationRequestDto reInvitationRequestDto) {
		log.info("sendReInvitation: execution started");

		if (!profileActivator.isEpProfile()) {
			Optional<OrganizationConfig> optionalOrganizationConfig = organizationConfigDao
				.findOrganizationConfigByOrganizationConfigType(OrganizationConfigType.EMAIL_CONFIGS);

			if (optionalOrganizationConfig.isEmpty()) {
				log.error("Email configuration not found");
				throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EMAIL_CONFIG_NOT_FOUND);
			}
		}

		List<Long> ids = reInvitationRequestDto.getIds();
		if (ids != null) {
			Set<Long> uniqueEmails = new HashSet<>(ids);
			ids = new ArrayList<>(uniqueEmails);
			reInvitationRequestDto.setIds(ids);
		}

		String currentTenant = bulkContextService.getContext();

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		List<ErrorLogDto> bulkRecordErrorLogs = Collections.synchronizedList(new ArrayList<>());
		ReInvitationSkippedCountDto reInvitationSkippedCountDto = new ReInvitationSkippedCountDto(0);
		BulkStatusSummaryDto bulkStatusSummary = new BulkStatusSummaryDto();

		List<CompletableFuture<Void>> tasks = new ArrayList<>();
		List<List<Long>> chunkedIds = CommonModuleUtils.chunkData(reInvitationRequestDto.getIds());
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

		for (List<Long> chunkedIdsChunkDtoList : chunkedIds) {
			for (Long id : chunkedIdsChunkDtoList) {
				CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
					bulkContextService.setContext(currentTenant);
					try {
						transactionTemplate.execute(new TransactionCallbackWithoutResult() {
							@Override
							protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
								validateAndSendReInvitation(id, reInvitationSkippedCountDto, bulkRecordErrorLogs,
										bulkStatusSummary);
							}
						});
					}
					catch (Exception e) {
						log.error("Exception occurred when saving entitlement: {}", e.getMessage());
						List<String> errorMessages = Collections.singletonList(e.getMessage());
						bulkRecordErrorLogs.add(createErrorLog(id, errorMessages));
						bulkStatusSummary.incrementFailedCount();
					}
				}, executorService);
				tasks.add(task);
			}
		}

		CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
		allTasks.thenRun(executorService::shutdown);
		allTasks.join();

		try {
			if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
				log.error("ExecutorService failed to terminate after 5 minutes, shutting down");
				executorService.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted while waiting for termination of ExecutorService", e);
		}

		BulkResponseDto responseDto = new BulkResponseDto();
		responseDto.setBulkRecordErrorLogs(bulkRecordErrorLogs);
		responseDto.setBulkStatusSummary(bulkStatusSummary);

		log.info("sendReInvitation: execution ended");
		return new ResponseEntityDto(false, responseDto);
	}

	@Override
	public ResponseEntityDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {
		log.info("forgotPassword: execution started");

		Validations.validateEmail(forgotPasswordRequestDto.getEmail());

		Optional<User> optionalUser = userDao.findByEmail(forgotPasswordRequestDto.getEmail());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		LocalDateTime nowUtc = DateTimeUtils.getCurrentUtcDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String requestDateTime = nowUtc.format(formatter);

		peopleEmailService.sendPasswordResetRequestManagerEmail(optionalUser.get(), requestDateTime);
		peopleNotificationService.sendPasswordResetRequestManagerNotification(optionalUser.get(), requestDateTime);

		log.info("forgotPassword: execution ended");
		return new ResponseEntityDto(false, "The email has been successfully sent to all people admins.");
	}

	@Override
	public ResponseEntityDto changePassword(ChangePasswordRequestDto changePasswordRequestDto, Long userId) {
		log.info("changePassword: execution started");

		User user = userService.getCurrentUser();
		if (!Objects.equals(user.getUserId(), userId)) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_OLD_PASSWORD_INCORRECT);
		}

		String newPassword = changePasswordRequestDto.getNewPassword();
		createNewPassword(newPassword, user);

		log.info("changePassword: execution ended");
		return new ResponseEntityDto(false, "User password changed successfully");
	}

	private boolean isSuperAdminExists() {
		return employeeRoleDao.existsByIsSuperAdminTrue();
	}

	private void createNewPassword(String newPassword, User user) {
		if (user.getPreviousPasswordsList()
			.stream()
			.anyMatch(prevPassword -> passwordEncoder.matches(newPassword, prevPassword))) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_CANNOT_USE_PREVIOUS_PASSWORDS);
		}

		String encodedNewPassword = passwordEncoder.encode(newPassword);

		if (user.getPassword() != null) {
			user.addPreviousPassword(user.getPassword());
		}

		user.setPassword(encodedNewPassword);
		user.setIsPasswordChangedForTheFirstTime(true);
		user.setTempPassword(null);

		userDao.save(user);
	}

	private void validateAndSendReInvitation(Long id, ReInvitationSkippedCountDto reInvitationSkippedCountDto,
			List<ErrorLogDto> bulkRecordErrorLogs, BulkStatusSummaryDto bulkStatusSummary) {
		List<String> errors = new ArrayList<>();

		Optional<User> optionalUser = userDao.findById(id);

		if (optionalUser.isEmpty()) {
			errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND,
					new String[] { String.valueOf(id) }));
		}
		else {
			if (optionalUser.get().getEmployee().getAccountStatus() != AccountStatus.PENDING) {
				errors.add(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_USER_ACCOUNT_ACTIVATED,
						new String[] { String.valueOf(id) }));
			}
		}

		if (!errors.isEmpty()) {
			reInvitationSkippedCountDto.incrementSkippedCount();
			bulkStatusSummary.incrementFailedCount();
			bulkRecordErrorLogs.add(createErrorLog(id, errors));
			return;
		}

		try {
			User user = optionalUser.get();
			Optional<User> firstUser = userDao.findById(1L);
			LoginMethod loginMethod = firstUser.isPresent() ? firstUser.get().getLoginMethod()
					: LoginMethod.CREDENTIALS;

			if (loginMethod.equals(LoginMethod.CREDENTIALS)) {
				String tempPassword = CommonModuleUtils.generateSecureRandomPassword();
				user.setTempPassword(encryptionDecryptionService.encrypt(tempPassword, encryptSecret));
				user.setPassword(passwordEncoder.encode(tempPassword));
			}

			userDao.save(user);
			peopleEmailService.sendUserInvitationEmail(user);
			bulkStatusSummary.incrementSuccessCount();
		}
		catch (Exception e) {
			log.error("Error re invitation for : {}, error: {}", id, e.getMessage());
			bulkStatusSummary.incrementFailedCount();
			bulkRecordErrorLogs.add(createErrorLog(id, List.of(e.getMessage())));
		}

	}

	private ErrorLogDto createErrorLog(Long id, List<String> errors) {
		ErrorLogDto errorLog = new ErrorLogDto();
		errorLog.setEmployeeId(id);
		errorLog.setStatus(BulkItemStatus.ERROR);
		errorLog.setMessage(String.join("; ", errors));
		return errorLog;
	}

}
