package com.skapp.community.leaveplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.model.User;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.leaveplanner.payload.request.LeaveRequestDto;
import com.skapp.community.leaveplanner.type.LeaveRequestStatus;
import com.skapp.community.leaveplanner.type.LeaveState;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
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
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class LeaveControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String PATH = "/v1/leave";

	@BeforeEach
	public void setup() {

		/**
		 * Set mocked user and a mocked security context
		 */
		User mockUser = new User();
		mockUser.setEmail("user2@gmail.com");
		mockUser.setPassword("$2a$12$CGe4n75Yejv/O8dnOTD7R.x0LruTiKM22kcdc3YNl4RRw01srJsB6");
		mockUser.setIsActive(true);
		mockUser.setUserId(2L);
		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(2L);
		mockEmployee.setFirstName("name");
		EmployeeRole role = new EmployeeRole();
		role.setLeaveRole(Role.LEAVE_EMPLOYEE);
		role.setIsSuperAdmin(true);
		mockEmployee.setEmployeeRole(role);
		mockUser.setEmployee(mockEmployee);

		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser, null,
				mockUser.getAuthorities());
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void applyLeaveRequest_returnsHttpStatusCreated() throws Exception {
		LeaveRequestDto leaveRequestDto = new LeaveRequestDto();

		leaveRequestDto.setStartDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(), 2, 12));
		leaveRequestDto.setEndDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(), 2, 13));

		leaveRequestDto.setTypeId(1L);
		leaveRequestDto.setRequestDesc("Full day leave");
		leaveRequestDto.setLeaveState(LeaveState.FULLDAY);

		mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(leaveRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['leaveType']['typeId']").value(1))
			.andExpect(jsonPath("['results'][0]['leaveState']").value(LeaveState.FULLDAY.name()))
			.andExpect(jsonPath("['results'][0]['status']").value(LeaveRequestStatus.PENDING.name()))
			.andExpect(jsonPath("['results'][0]['startDate']").isNotEmpty())
			.andExpect(jsonPath("['results'][0]['endDate']").isNotEmpty());
	}

	@Test
	void applyLeaveRequest_commentMandatory_returnsHttpStatusBadRequest() throws Exception {
		LeaveRequestDto leaveRequestDto = new LeaveRequestDto();

		leaveRequestDto.setStartDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(), 2, 12));
		leaveRequestDto.setEndDate(DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(), 2, 12));

		leaveRequestDto.setTypeId(6L);
		leaveRequestDto.setLeaveState(LeaveState.HALFDAY_MORNING);

		mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(leaveRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value("Comment must be included for the selected leave type"));
	}

}
