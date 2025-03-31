package com.skapp.community.peopleplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.model.User;
import com.skapp.community.common.security.AuthorityService;
import com.skapp.community.common.security.SkappUserDetails;
import com.skapp.community.common.service.JwtService;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.peopleplanner.constant.PeopleMessageConstant;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.employee.CreateEmployeeRequestDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeCommonDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeEmploymentDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeePersonalDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeSystemPermissionsDto;
import com.skapp.community.peopleplanner.payload.request.employee.employment.EmployeeEmploymentBasicDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.employment.EmployeeEmploymentBasicDetailsManagerDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.employment.EmployeeEmploymentIdentificationAndDiversityDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalContactDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalGeneralDetailsDto;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.EEO;
import com.skapp.community.peopleplanner.type.EmploymentAllocation;
import com.skapp.community.peopleplanner.type.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("People Controller Integration Tests")
class PeopleControllerIntegrationTest {

	private static final String STATUS_PATH = "['status']";

	private static final String RESULTS_0_PATH = "['results'][0]";

	private static final String MESSAGE_PATH = "['message']";

	private static final String STATUS_UNSUCCESSFUL = "unsuccessful";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AuthorityService authorityService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private MessageUtil messageUtil;

	private String authToken;

	@BeforeEach
	void setup() {
		setupSecurityContext();
		authToken = jwtService.generateAccessToken(userDetailsService.loadUserByUsername("user1@gmail.com"), 1L);
	}

	private RequestPostProcessor bearerToken() {
		return request -> {
			request.addHeader("Authorization", "Bearer " + authToken);
			return request;
		};
	}

	private ResultActions performRequest(MockHttpServletRequestBuilder request) throws Exception {
		return mvc.perform(request.with(bearerToken()));
	}

	private <T> ResultActions performPostRequest(T content) throws Exception {
		return performRequest(post("/v1/people/employee").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(content))
			.accept(MediaType.APPLICATION_JSON));
	}

	private <T> ResultActions performPatchRequest(T content) throws Exception {
		return performRequest(patch("/v1/people/employee/100").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(content))
			.accept(MediaType.APPLICATION_JSON));
	}

	private void setupSecurityContext() {
		User mockUser = createMockUser();
		SkappUserDetails userDetails = SkappUserDetails.builder()
			.username(mockUser.getEmail())
			.password(mockUser.getPassword())
			.enabled(mockUser.getIsActive())
			.authorities(authorityService.getAuthorities(mockUser))
			.build();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	private User createMockUser() {
		User mockUser = new User();
		mockUser.setEmail("user1@gmail.com");
		mockUser.setPassword("$2a$12$CGe4n75Yejv/O8dnOTD7R.x0LruTiKM22kcdc3YNl4RRw01srJsB6");
		mockUser.setIsActive(true);

		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(1L);
		mockEmployee.setFirstName("name");

		EmployeeRole role = new EmployeeRole();
		role.setAttendanceRole(Role.ATTENDANCE_ADMIN);
		role.setPeopleRole(Role.PEOPLE_ADMIN);
		role.setLeaveRole(Role.LEAVE_ADMIN);
		role.setIsSuperAdmin(true);

		mockEmployee.setEmployeeRole(role);
		mockUser.setEmployee(mockEmployee);

		return mockUser;
	}

	private CreateEmployeeRequestDto createEmployeeDetails() {
		CreateEmployeeRequestDto createEmployeeRequestDto = new CreateEmployeeRequestDto();

		EmployeePersonalDetailsDto employeePersonalDetailsDto = new EmployeePersonalDetailsDto();
		EmployeePersonalGeneralDetailsDto employeePersonalGeneralDetailsDto = new EmployeePersonalGeneralDetailsDto();
		employeePersonalGeneralDetailsDto.setFirstName("Employee");
		employeePersonalGeneralDetailsDto.setLastName("Lastname");
		employeePersonalGeneralDetailsDto.setMiddleName("MiddleName");
		employeePersonalGeneralDetailsDto.setNin("P74");
		employeePersonalGeneralDetailsDto.setGender(Gender.MALE);

		EmployeePersonalContactDetailsDto employeePersonalContactDetailsDto = new EmployeePersonalContactDetailsDto();
		employeePersonalContactDetailsDto.setPersonalEmail("employee5@gmail.com");
		employeePersonalContactDetailsDto.setContactNo("0773696445");
		employeePersonalContactDetailsDto.setAddressLine1("Address line 1");
		employeePersonalContactDetailsDto.setAddressLine2("Address line 2");
		employeePersonalContactDetailsDto.setCountry("USA");

		employeePersonalDetailsDto.setGeneral(employeePersonalGeneralDetailsDto);
		employeePersonalDetailsDto.setContact(employeePersonalContactDetailsDto);

		EmployeeEmploymentDetailsDto employeeEmploymentDetailsDto = new EmployeeEmploymentDetailsDto();
		EmployeeEmploymentBasicDetailsDto employeeEmploymentBasicDetailsDto = getEmployeeEmploymentBasicDetailsDto();

		EmployeeEmploymentIdentificationAndDiversityDetailsDto employeeEmploymentIdentificationAndDiversityDetailsDto = new EmployeeEmploymentIdentificationAndDiversityDetailsDto();
		employeeEmploymentIdentificationAndDiversityDetailsDto.setEeoJobCategory(EEO.PROFESSIONALS);

		employeeEmploymentDetailsDto.setEmploymentDetails(employeeEmploymentBasicDetailsDto);
		employeeEmploymentDetailsDto
			.setIdentificationAndDiversityDetails(employeeEmploymentIdentificationAndDiversityDetailsDto);

		EmployeeSystemPermissionsDto employeeSystemPermissionsDto = new EmployeeSystemPermissionsDto();
		employeeSystemPermissionsDto.setEsignRole(Role.ESIGN_EMPLOYEE);
		employeeSystemPermissionsDto.setLeaveRole(Role.LEAVE_EMPLOYEE);
		employeeSystemPermissionsDto.setAttendanceRole(Role.ATTENDANCE_EMPLOYEE);
		employeeSystemPermissionsDto.setPeopleRole(Role.PEOPLE_EMPLOYEE);
		employeeSystemPermissionsDto.setIsSuperAdmin(false);

		EmployeeCommonDetailsDto employeeCommonDetailsDto = new EmployeeCommonDetailsDto();
		employeeCommonDetailsDto.setAccountStatus(AccountStatus.ACTIVE);
		employeeCommonDetailsDto.setJobTitle("Software Engineer");

		createEmployeeRequestDto.setPersonal(employeePersonalDetailsDto);
		createEmployeeRequestDto.setEmployment(employeeEmploymentDetailsDto);
		createEmployeeRequestDto.setCommon(employeeCommonDetailsDto);
		createEmployeeRequestDto.setSystemPermissions(employeeSystemPermissionsDto);

		return createEmployeeRequestDto;
	}

	private static EmployeeEmploymentBasicDetailsDto getEmployeeEmploymentBasicDetailsDto() {
		EmployeeEmploymentBasicDetailsDto employeeEmploymentBasicDetailsDto = new EmployeeEmploymentBasicDetailsDto();
		employeeEmploymentBasicDetailsDto.setEmail("username9@gmail.com");
		employeeEmploymentBasicDetailsDto.setWorkTimeZone("AST");

		EmployeeEmploymentBasicDetailsManagerDetailsDto employeeEmploymentBasicDetailsPrimaryManagerDetailsDto = new EmployeeEmploymentBasicDetailsManagerDetailsDto();
		employeeEmploymentBasicDetailsPrimaryManagerDetailsDto.setEmployeeId(1L);
		employeeEmploymentBasicDetailsPrimaryManagerDetailsDto.setFirstName("Primary Manager Name 1");
		employeeEmploymentBasicDetailsPrimaryManagerDetailsDto.setLastName("Primary Manager Name 2");

		EmployeeEmploymentBasicDetailsManagerDetailsDto employeeEmploymentBasicDetailsSecondaryManagerDetailsDto = new EmployeeEmploymentBasicDetailsManagerDetailsDto();
		employeeEmploymentBasicDetailsSecondaryManagerDetailsDto.setEmployeeId(1L);
		employeeEmploymentBasicDetailsSecondaryManagerDetailsDto.setFirstName("Primary Manager Name 1");
		employeeEmploymentBasicDetailsSecondaryManagerDetailsDto.setLastName("Primary Manager Name 2");

		employeeEmploymentBasicDetailsDto.setPrimarySupervisor(employeeEmploymentBasicDetailsPrimaryManagerDetailsDto);
		employeeEmploymentBasicDetailsDto
			.setSecondarySupervisor(employeeEmploymentBasicDetailsSecondaryManagerDetailsDto);

		Long[] teamIds = { 1L };
		employeeEmploymentBasicDetailsDto.setTeamIds(teamIds);

		employeeEmploymentBasicDetailsDto.setProbationStartDate(LocalDate.parse("2021-10-10"));
		employeeEmploymentBasicDetailsDto.setProbationEndDate(LocalDate.parse("2021-12-28"));
		employeeEmploymentBasicDetailsDto
			.setJoinedDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear() - 1, 1, 1));

		employeeEmploymentBasicDetailsDto.setEmploymentAllocation(EmploymentAllocation.FULL_TIME);
		return employeeEmploymentBasicDetailsDto;
	}

	private static EmployeeEmploymentBasicDetailsDto getEmploymentBasicDetailsDto() {
		EmployeeEmploymentBasicDetailsDto employeeEmploymentBasicDetailsDto = new EmployeeEmploymentBasicDetailsDto();
		employeeEmploymentBasicDetailsDto.setEmail("username20@gmail.com");

		EmployeeEmploymentBasicDetailsManagerDetailsDto employeeEmploymentBasicDetailsPrimaryManagerDetailsDto = new EmployeeEmploymentBasicDetailsManagerDetailsDto();
		employeeEmploymentBasicDetailsPrimaryManagerDetailsDto.setEmployeeId(25L);
		employeeEmploymentBasicDetailsPrimaryManagerDetailsDto.setFirstName("Primary Manager Name 1");
		employeeEmploymentBasicDetailsPrimaryManagerDetailsDto.setLastName("Primary Manager Name 2");
		employeeEmploymentBasicDetailsDto.setPrimarySupervisor(employeeEmploymentBasicDetailsPrimaryManagerDetailsDto);
		return employeeEmploymentBasicDetailsDto;
	}

	@Nested
	@DisplayName("Employee Creation Tests")
	class EmployeeCreationTests {

		@Test
		@DisplayName("Add employee with invalid managers - Returns Not Found")
		void addEmployee_WithInvalidManagers_ReturnsEntityNotFound() throws Exception {
			CreateEmployeeRequestDto createEmployeeRequestDto = createEmployeeDetails();

			EmployeeEmploymentDetailsDto employeeEmploymentDetailsDto = new EmployeeEmploymentDetailsDto();
			EmployeeEmploymentBasicDetailsDto employeeEmploymentBasicDetailsDto = getEmploymentBasicDetailsDto();
			employeeEmploymentDetailsDto.setEmploymentDetails(employeeEmploymentBasicDetailsDto);

			createEmployeeRequestDto.setEmployment(employeeEmploymentDetailsDto);

			performPostRequest(createEmployeeRequestDto).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH).value(messageUtil
					.getMessage(PeopleMessageConstant.PEOPLE_ERROR_VALIDATION_PRIMARY_SUPERVISOR_EMPLOYEE_NOT_FOUND)));
		}

		@Test
		@DisplayName("Add employee with invalid last name - Returns Bad Request")
		void addEmployee_WithInvalidLastName_ReturnsBadRequest() throws Exception {

			CreateEmployeeRequestDto createEmployeeRequestDto = createEmployeeDetails();
			EmployeePersonalDetailsDto employeePersonalDetailsDto = new EmployeePersonalDetailsDto();

			EmployeePersonalGeneralDetailsDto employeePersonalGeneralDetailsDto = new EmployeePersonalGeneralDetailsDto();
			employeePersonalGeneralDetailsDto.setFirstName("first name");
			employeePersonalGeneralDetailsDto.setLastName("last name 456");

			employeePersonalDetailsDto.setGeneral(employeePersonalGeneralDetailsDto);
			createEmployeeRequestDto.setPersonal(employeePersonalDetailsDto);

			performPostRequest(createEmployeeRequestDto).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH)
					.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_LAST_NAME)));
		}

		@Test
		@DisplayName("Add employee with invalid first name - Returns Bad Request")
		void addEmployee_WithInvalidFirstName_ReturnsBadRequest() throws Exception {
			CreateEmployeeRequestDto createEmployeeRequestDto = createEmployeeDetails();
			EmployeePersonalDetailsDto employeePersonalDetailsDto = new EmployeePersonalDetailsDto();

			EmployeePersonalGeneralDetailsDto employeePersonalGeneralDetailsDto = new EmployeePersonalGeneralDetailsDto();
			employeePersonalGeneralDetailsDto.setFirstName("first name 123");
			employeePersonalGeneralDetailsDto.setLastName("last name");

			employeePersonalDetailsDto.setGeneral(employeePersonalGeneralDetailsDto);
			createEmployeeRequestDto.setPersonal(employeePersonalDetailsDto);

			performPostRequest(createEmployeeRequestDto).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH)
					.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_FIRST_NAME)));
		}

	}

	@Nested
	@DisplayName("Employee Update Tests")
	class EmployeeUpdateTests {

		@Test
		@DisplayName("Update non-existent employee - Returns Not Found")
		void updateEmployee_WithNonExistentUser_ReturnsNotFound() throws Exception {
			EmployeeUpdateDto updateDto = new EmployeeUpdateDto();
			updateDto.setFirstName("newName");

			performPatchRequest(updateDto).andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH)
					.value(messageUtil.getMessage(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_NOT_FOUND)));
		}

	}

}
