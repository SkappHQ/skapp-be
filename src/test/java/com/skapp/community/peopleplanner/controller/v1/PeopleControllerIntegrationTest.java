package com.skapp.community.peopleplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.model.User;
import com.skapp.community.common.security.AuthorityService;
import com.skapp.community.common.security.SkappUserDetails;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.ProbationPeriodDto;
import com.skapp.community.peopleplanner.payload.request.RoleRequestDto;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.EEO;
import com.skapp.community.peopleplanner.type.EmploymentAllocation;
import com.skapp.community.peopleplanner.type.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class PeopleControllerIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AuthorityService authorityService;

	@Autowired
	private MockMvc mvc;

	private final String path = "/v1/people";

	@Autowired
	private MessageUtil messageUtil;

	@BeforeEach
	public void setup() {

		// Set mocked user and a mocked security context
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

		SkappUserDetails userDetails = SkappUserDetails.builder()
			.username(mockUser.getEmail())
			.password(mockUser.getPassword())
			.enabled(mockUser.getIsActive())
			.authorities(authorityService.getAuthorities(mockUser))
			.build();

		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void addEmployee_withInvalidManagers_returnsEntityNotFound() throws Exception {
		EmployeeDetailsDto employeeDetailsDto = getEmployeeDetails();
		employeeDetailsDto.setWorkEmail("username20@gmail.com");
		employeeDetailsDto.setPrimaryManager(25L);

		mvc.perform(post(path.concat("/employee")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(employeeDetailsDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Manager not found"));
	}

	@Test
	void addEmployee_withInvalidLastName_returnsBadRequest() throws Exception {
		EmployeeDetailsDto employeeDetailsDto = getEmployeeDetails();
		employeeDetailsDto.setFirstName("first name");
		employeeDetailsDto.setLastName("last name 456");

		mvc.perform(post(path.concat("/employee")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(employeeDetailsDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMPLOYEE_NAME)));
	}

	@Test
	void addEmployee_withInvalidFirstName_returnsBadRequest() throws Exception {
		EmployeeDetailsDto employeeDetailsDto = getEmployeeDetails();
		employeeDetailsDto.setFirstName("first name 123");
		employeeDetailsDto.setLastName("last name");

		mvc.perform(post(path.concat("/employee")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(employeeDetailsDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMPLOYEE_NAME)));
	}

	@Test
	void deleteEmployeeByIdApi_withoutUserNotExists_returnsHttpStatusNotFound() throws Exception {
		EmployeeUpdateDto updateDto = new EmployeeUpdateDto();
		updateDto.setFirstName("newName");
		mvc.perform(patch(path.concat("/employee/100")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("User not found"));
	}

	private EmployeeDetailsDto getEmployeeDetails() {
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
		RoleRequestDto roleRequestDto = new RoleRequestDto();
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

}
