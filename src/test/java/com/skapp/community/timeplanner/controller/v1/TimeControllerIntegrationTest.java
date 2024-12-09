package com.skapp.community.timeplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.model.User;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.EmploymentAllocation;
import com.skapp.community.peopleplanner.type.RequestStatus;
import com.skapp.community.peopleplanner.type.RequestType;
import com.skapp.community.timeplanner.payload.request.AddTimeRecordDto;
import com.skapp.community.timeplanner.payload.request.ManualEntryRequestDto;
import com.skapp.community.timeplanner.payload.request.TimeRequestManagerPatchDto;
import com.skapp.community.timeplanner.type.TimeRecordActionTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimeControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String PATH = "/v1/time";

	@BeforeEach
	public void setup() {

		/**
		 * Set mocked user and a mocked security context
		 */
		User mockUser = new User();
		mockUser.setEmail("user1@gmail.com");
		mockUser.setPassword("$2a$12$CGe4n75Yejv/O8dnOTD7R.x0LruTiKM22kcdc3YNl4RRw01srJsB6");
		mockUser.setUserId(1L);
		mockUser.setIsActive(true);

		User mockManagerUser = new User();
		mockManagerUser.setEmail("user2@gmail.com");
		mockManagerUser.setPassword("$2a$12$Z6/UrecHPvvCBVj/kEeGWezwhMzg46fPSJiAr/sLnBxhDAZfF4/1W");
		mockManagerUser.setUserId(2L);
		mockManagerUser.setIsActive(true);

		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(1L);
		mockEmployee.setFirstName("name");
		mockEmployee.setAccountStatus(AccountStatus.ACTIVE);
		mockEmployee.setEmploymentAllocation(EmploymentAllocation.FULL_TIME);

		Employee managerEmployee = new Employee();
		managerEmployee.setEmployeeId(2L);
		managerEmployee.setFirstName("name");
		managerEmployee.setAccountStatus(AccountStatus.ACTIVE);
		managerEmployee.setEmploymentAllocation(EmploymentAllocation.FULL_TIME);
		managerEmployee.setUser(mockManagerUser);

		EmployeeManager employeeManager = new EmployeeManager();
		employeeManager.setEmployee(mockEmployee);
		employeeManager.setManager(managerEmployee);
		Set<EmployeeManager> managerSet = new HashSet<>();
		managerSet.add(employeeManager);
		mockEmployee.setManagers(managerSet);
		EmployeeRole role = new EmployeeRole();
		role.setEmployeeRoleId(1L);
		role.setAttendanceRole(Role.SUPER_ADMIN);
		role.setIsSuperAdmin(true);
		mockEmployee.setEmployeeRole(role);
		mockUser.setEmployee(mockEmployee);
		mockEmployee.setUser(mockUser);

		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser, null,
				mockUser.getAuthorities());
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	@Order(1)
	void getActiveTimeSlotWhenTimeRecordAvailable_ButClockedOut_ReturnsHTTPSBadRequest() throws Exception {
		mvc.perform(get(PATH.concat("/active-slot")).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['periodType']").value("END"));

	}

	@Test
	@Order(3)
	void addTimeLog_forTheCurrentDay_CLOCK_IN_returnsHttpStatusCreated() throws Exception {

		LocalDateTime startTime = DateTimeUtils.getCurrentUtcDateTime().minusDays(1L);
		AddTimeRecordDto addTimeRecordDto = new AddTimeRecordDto();
		addTimeRecordDto.setRecordActionType(TimeRecordActionTypes.START);
		addTimeRecordDto.setTime(startTime);

		mvc.perform(post(PATH.concat("/record")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(addTimeRecordDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]").value("Time Record Added Successfully " + startTime + " START"));

	}

	@Test
	void addTimeLog_forTheCurrentDay_when_CLOCK_IN_exists_returnsHttpStatusFail() throws Exception {
		AddTimeRecordDto addTimeRecordDto = new AddTimeRecordDto();
		addTimeRecordDto.setRecordActionType(TimeRecordActionTypes.START);
		addTimeRecordDto.setTime(DateTimeUtils.getCurrentUtcDateTime());

		mvc.perform(post(PATH.concat("/record")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(addTimeRecordDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Clock in already exists for the current date"));

	}

	@Test
	void addTimeLog_forTheCurrentDay_WORK_request_when_no_CLOCK_IN_exists_returnsHttpStatusfail() throws Exception {
		AddTimeRecordDto addTimeRecordDto = new AddTimeRecordDto();
		addTimeRecordDto.setRecordActionType(TimeRecordActionTypes.RESUME);
		addTimeRecordDto.setTime(DateTimeUtils.getCurrentUtcDateTime().minusDays(2L));

		mvc.perform(post(PATH.concat("/record")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(addTimeRecordDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Clock in does not exists for the current date"));
	}

	@Test
	@Order(2)
	void getActiveTimeSlot_ReturnsOk() throws Exception {
		mvc.perform(get(PATH.concat("/active-slot")).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results']").isNotEmpty());

	}

	@Test
	void addManualEntryRequest_invalidStartEndDate_startTimeAfterEndTime_returnsBadRequest() throws Exception {
		LocalDateTime startTime = LocalDateTime.of(DateTimeUtils.getCurrentYear(), 1, 1, 8, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(DateTimeUtils.getCurrentYear(), 1, 1, 7, 30, 0);

		ManualEntryRequestDto manualEntryRequestDto = new ManualEntryRequestDto();
		manualEntryRequestDto.setRequestType(RequestType.MANUAL_ENTRY_REQUEST);
		manualEntryRequestDto.setStartTime(startTime);
		manualEntryRequestDto.setEndTime(endTime);
		manualEntryRequestDto.setRecordId(1L);
		manualEntryRequestDto.setZoneId(String.valueOf(ZoneId.systemDefault()));

		mvc.perform(post(PATH.concat("/manual-entry")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(manualEntryRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Start time cannot be after end time"));
	}

	@Test
	void addManualEntryRequest_invalidStartEndDate_differentDate_returnsBadRequest() throws Exception {
		LocalDateTime startTime = LocalDateTime.of(DateTimeUtils.getCurrentYear(), 1, 1, 23, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(DateTimeUtils.getCurrentYear(), 1, 2, 0, 30, 0);

		ManualEntryRequestDto manualEntryRequestDto = new ManualEntryRequestDto();
		manualEntryRequestDto.setRequestType(RequestType.MANUAL_ENTRY_REQUEST);
		manualEntryRequestDto.setStartTime(startTime);
		manualEntryRequestDto.setEndTime(endTime);
		manualEntryRequestDto.setRecordId(1L);
		manualEntryRequestDto.setZoneId(String.valueOf(ZoneId.systemDefault()));

		mvc.perform(post(PATH.concat("/manual-entry")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(manualEntryRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(
					jsonPath("['results'][0]['message']").value("Start time and End time must be within the same day"));
	}

	@Test
	void addManualEntryRequest_withoutRequestType_returnsOk() throws Exception {
		LocalDateTime startTime = LocalDateTime.of(DateTimeUtils.getCurrentYear(), 2, 27, 5, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(DateTimeUtils.getCurrentYear(), 2, 27, 6, 30, 0);

		ManualEntryRequestDto manualEntryRequestDto = new ManualEntryRequestDto();
		manualEntryRequestDto.setStartTime(startTime);
		manualEntryRequestDto.setEndTime(endTime);
		manualEntryRequestDto.setRecordId(3L);
		manualEntryRequestDto.setZoneId(ZoneId.systemDefault().getId());

		mvc.perform(post(PATH.concat("/manual-entry")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(manualEntryRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['requestType']").value("MANUAL_ENTRY_REQUEST"));
	}

	@Test
	void updateTimeRequestByManager_withValidTimeRequestId() throws Exception {
		TimeRequestManagerPatchDto timeRequestManagerPatchDto = new TimeRequestManagerPatchDto();
		timeRequestManagerPatchDto.setStatus(RequestStatus.APPROVED);

		mvc.perform(patch(PATH.concat("/time-request/1")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(timeRequestManagerPatchDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['status']").value("APPROVED"));
	}

	@Test
	void updateTimeRequestByManager_withInValidTimeRequestId() throws Exception {
		TimeRequestManagerPatchDto timeRequestManagerPatchDto = new TimeRequestManagerPatchDto();
		timeRequestManagerPatchDto.setStatus(RequestStatus.APPROVED);

		mvc.perform(patch(PATH.concat("/time-request/100")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(timeRequestManagerPatchDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"));
	}

	// @Test
	// void
	// individualWorkTimeUtilizationByAdmin_notSupervisingEmployee_returnsHttpStatusOk()
	// throws Exception {
	// mvc.perform(get(PATH.concat("/individual-utilization/1")).contentType(MediaType.APPLICATION_JSON)
	// .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
	// }

	@Test
	void managerTeamTimeRecordSummary_withInvalidDateRange_returnsBadRequest() throws Exception {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

		queryParams.add("startDate",
				String.valueOf(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(), 3, 30)));
		queryParams.add("endDate",
				String.valueOf(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(), 3, 29)));
		queryParams.add("teamId", "1");
		queryParams.add("filterTime", "DATE_RANGE");
		mvc.perform(get(PATH.concat("/team-time-record-summary")).contentType(MediaType.APPLICATION_JSON)
			.params(queryParams)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['results'][0]['message']").value("Start date and end date are not valid"));
	}

}
