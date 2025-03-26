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
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.ProbationPeriodDto;
import com.skapp.community.peopleplanner.payload.request.employee.EmployeeSystemPermissionsDto;
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
import java.util.HashSet;
import java.util.Set;

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

	private EmployeeDetailsDto createEmployeeDetails() {
		EmployeeDetailsDto employeeDetailsDto = new EmployeeDetailsDto();
		employeeDetailsDto.setWorkEmail("username9@gmail.com");
		employeeDetailsDto.setFirstName("Employee");
		employeeDetailsDto.setLastName("Lastname");
		employeeDetailsDto.setMiddleName("MiddleName");
		employeeDetailsDto.setDesignation("Software Engineer");
		employeeDetailsDto.setCountry("USA");
		employeeDetailsDto.setPersonalEmail("employee5@gmail.com");
		employeeDetailsDto.setPhone("0773696445");
		employeeDetailsDto.setIdentificationNo("P74");
		employeeDetailsDto.setTimeZone("AST");
		employeeDetailsDto.setAddress("Address");
		employeeDetailsDto.setAddressLine2("Address line 2");
		employeeDetailsDto.setAccountStatus(AccountStatus.ACTIVE);
		employeeDetailsDto.setEmploymentAllocation(EmploymentAllocation.FULL_TIME);
		employeeDetailsDto.setEeo(EEO.PROFESSIONALS);
		employeeDetailsDto.setPrimaryManager(1L);
		employeeDetailsDto.setSecondaryManager(3L);

		EmployeeSystemPermissionsDto roleRequestDto = new EmployeeSystemPermissionsDto();
		roleRequestDto.setAttendanceRole(Role.ATTENDANCE_EMPLOYEE);
		roleRequestDto.setLeaveRole(Role.LEAVE_EMPLOYEE);
		roleRequestDto.setPeopleRole(Role.PEOPLE_EMPLOYEE);
		roleRequestDto.setIsSuperAdmin(false);
		employeeDetailsDto.setUserRoles(roleRequestDto);

		Set<Long> teamIds = new HashSet<>();
		teamIds.add(1L);
		employeeDetailsDto.setTeams(teamIds);

		ProbationPeriodDto probationPeriodDto = new ProbationPeriodDto();
		probationPeriodDto.setStartDate(LocalDate.parse("2021-10-10"));
		probationPeriodDto.setEndDate(LocalDate.parse("2021-12-28"));
		employeeDetailsDto.setProbationPeriod(probationPeriodDto);

		employeeDetailsDto.setGender(Gender.MALE);
		employeeDetailsDto.setJoinDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear() - 1, 1, 1));

		ProbationPeriodDto employeePeriodDto = new ProbationPeriodDto();
		employeePeriodDto.setStartDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear() - 1, 2, 1));
		employeePeriodDto.setEndDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear() - 1, 9, 1));
		employeeDetailsDto.setEmployeePeriod(employeePeriodDto);

		employeeDetailsDto.setEmploymentAllocation(EmploymentAllocation.FULL_TIME);
		employeeDetailsDto.setAccountStatus(AccountStatus.ACTIVE);

		return employeeDetailsDto;
	}

	@Nested
	@DisplayName("Employee Creation Tests")
	class EmployeeCreationTests {

		@Test
		@DisplayName("Add employee with invalid managers - Returns Not Found")
		void addEmployee_WithInvalidManagers_ReturnsEntityNotFound() throws Exception {
			EmployeeDetailsDto employeeDetailsDto = createEmployeeDetails();
			employeeDetailsDto.setWorkEmail("username20@gmail.com");
			employeeDetailsDto.setPrimaryManager(25L);

			performPostRequest(employeeDetailsDto).andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH).value("Manager not found"));
		}

		@Test
		@DisplayName("Add employee with invalid last name - Returns Bad Request")
		void addEmployee_WithInvalidLastName_ReturnsBadRequest() throws Exception {
			EmployeeDetailsDto employeeDetailsDto = createEmployeeDetails();
			employeeDetailsDto.setFirstName("first name");
			employeeDetailsDto.setLastName("last name 456");

			performPostRequest(employeeDetailsDto).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH)
					.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMPLOYEE_NAME)));
		}

		@Test
		@DisplayName("Add employee with invalid first name - Returns Bad Request")
		void addEmployee_WithInvalidFirstName_ReturnsBadRequest() throws Exception {
			EmployeeDetailsDto employeeDetailsDto = createEmployeeDetails();
			employeeDetailsDto.setFirstName("first name 123");
			employeeDetailsDto.setLastName("last name");

			performPostRequest(employeeDetailsDto).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath(STATUS_PATH).value(STATUS_UNSUCCESSFUL))
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH)
					.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMPLOYEE_NAME)));
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
				.andExpect(jsonPath(RESULTS_0_PATH + MESSAGE_PATH).value("User not found"));
		}

	}

}
