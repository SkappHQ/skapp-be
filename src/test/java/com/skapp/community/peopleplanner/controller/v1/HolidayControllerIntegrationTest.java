package com.skapp.community.peopleplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.model.User;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.payload.request.HolidayBulkRequestDto;
import com.skapp.community.peopleplanner.payload.request.HolidayRequestDto;
import com.skapp.community.peopleplanner.payload.request.HolidaysDeleteRequestDto;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class HolidayControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String PATH = "/v1/holiday";

	@BeforeEach
	public void setup() {

		/**
		 * Set mocked user and a mocked security context
		 */
		User mockUser = new User();
		mockUser.setEmail("user1@gmail.com");
		mockUser.setPassword("$2a$12$CGe4n75Yejv/O8dnOTD7R.x0LruTiKM22kcdc3YNl4RRw01srJsB6");
		mockUser.setIsActive(true);
		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(1L);
		mockEmployee.setFirstName("name");
		EmployeeRole role = new EmployeeRole();
		role.setAttendanceRole(Role.SUPER_ADMIN);
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
	void getAllHolidays_returnSuccessful() throws Exception {

		mvc.perform(get(PATH).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['status']").value("successful"))
			.andExpect(jsonPath("$['results'][0]['items'][0]['id']").value(4))
			.andExpect(jsonPath("$['results'][0]['items'][1]['id']").value(5))
			.andExpect(jsonPath("$['results'][0]['items'][2]['id']").value(1))
			.andReturn();
	}

	@Test
	void getAllHolidays_withFiltration_returnSuccessful() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("isPagination", String.valueOf(true));
		params.add("holidayDurations", "FULL_DAY");

		mvc.perform(get(PATH).params(params).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['items'][0]['id']").value(4))
			.andExpect(jsonPath("['results'][0]['items'][1]['id']").value(5))
			.andExpect(jsonPath("['results'][0]['currentPage']").value("0"))
			.andExpect(jsonPath("['results'][0]['totalItems']").value("5"))
			.andExpect(jsonPath("['results'][0]['totalPages']").value("1"))
			.andReturn();
	}

	@Test
	void saveBulkHolidays_returnCreated() throws Exception {
		List<HolidayRequestDto> holidayDtoList = new ArrayList<>();
		holidayDtoList
			.add(getHolidayDto(String.format("%d-11-30", DateTimeUtils.getCurrentYear()), "Poya day", "FULL_DAY"));
		holidayDtoList
			.add(getHolidayDto(String.format("%d-11-29", DateTimeUtils.getCurrentYear()), "Christmas Day", "FULL_DAY"));

		HolidayBulkRequestDto holidayBulkRequestDto = new HolidayBulkRequestDto();
		holidayBulkRequestDto.setYear(DateTimeUtils.getCurrentYear());
		holidayBulkRequestDto.setHolidayDtoList(holidayDtoList);

		mvc.perform(post(PATH.concat("/bulk")).content(objectMapper.writeValueAsString(holidayBulkRequestDto))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$['status']").value("successful"))
			.andReturn();
	}

	@Test
	void deleteSelectedHolidays_returnSuccessful() throws Exception {
		HolidaysDeleteRequestDto holidaysDeleteRequestDto = new HolidaysDeleteRequestDto();
		List<Long> holidayIds = List.of(7L);
		holidaysDeleteRequestDto.setHolidayIds(holidayIds);

		mvc.perform(delete(PATH.concat("/selected")).content(objectMapper.writeValueAsString(holidaysDeleteRequestDto))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['status']").value("successful"))
			.andExpect(jsonPath("$['results'][0]['message']").value("Selected holidays deleted successfully."))
			.andReturn();
	}

	@Test
	void deleteAllHolidays_returnSuccessful() throws Exception {

		mvc.perform(delete(PATH.concat("/" + DateTimeUtils.getCurrentYear())).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['status']").value("successful"))
			.andExpect(jsonPath("$['results'][0]['message']").value("holidays deleted successfully"))
			.andReturn();
	}

	private HolidayRequestDto getHolidayDto(String date, String reason, String holidayDuration) {
		HolidayRequestDto holidayDto = new HolidayRequestDto();
		holidayDto.setDate(date);
		holidayDto.setName(reason);
		holidayDto.setHolidayDuration(holidayDuration);
		return holidayDto;
	}

}
