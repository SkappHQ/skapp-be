package com.skapp.community.leaveplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.model.User;
import com.skapp.community.common.type.Role;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.Month;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class LeaveAnalyticsControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String PATH = "/v1/leave/analytics";

	@BeforeEach
	public void setup() {

		/**
		 * Set mocked user and a mocked security context
		 */
		User mockUser = new User();
		mockUser.setUserId(1L);
		mockUser.setEmail("user1@gmail.com");
		mockUser.setPassword("$2a$12$CGe4n75Yejv/O8dnOTD7R.x0LruTiKM22kcdc3YNl4RRw01srJsB6");
		mockUser.setIsActive(true);
		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(1L);
		mockEmployee.setFirstName("name");
		EmployeeRole role = new EmployeeRole();
		role.setLeaveRole(Role.LEAVE_ADMIN);
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
	void getAllLeaveRequestsForPendingRequests_returnsHttpStatusOk() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("fetchType", "ALL");
		params.add("startDate", String.valueOf(LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1)));
		params.add("endDate", String.valueOf(LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 30)));
		params.add("status", "PENDING");

		mvc.perform(get(PATH.concat("/all/leaves")).params(params).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['results'][0]['items'][0]['status']").value("PENDING"));
	}

	@Test
	void getAllLeaveRequestsForApprovedRequests_returnsHttpStatusOk() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("fetchType", "ALL");
		params.add("startDate", String.valueOf(LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1)));
		params.add("endDate", String.valueOf(LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 30)));
		params.add("status", "APPROVED");

		mvc.perform(get(PATH.concat("/all/leaves")).params(params).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['results'][0]['items'][0]['status']").value("APPROVED"));
	}

	@Test
	void getAllLeaveRequestsWithSearchKeyword_returnsHttpStatusOk() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("fetchType", "ALL");
		params.add("startDate", String.valueOf(LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1)));
		params.add("endDate", String.valueOf(LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 30)));
		params.add("status", "PENDING");
		params.add("searchKeyword", "Lastname Two");

		mvc.perform(get(PATH.concat("/all/leaves")).params(params).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['results'][0]['items'][0]['status']").value("PENDING"))
			.andExpect(jsonPath("['results'][0]['items'][0]['employee']['lastName']").value("Lastname Two"));
	}

}
