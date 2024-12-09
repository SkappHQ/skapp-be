package com.skapp.community.common.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.request.OrganizationDto;
import com.skapp.community.common.payload.request.UpdateOrganizationRequestDto;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
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
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String PATH = "/v1/organization";

	@Autowired
	private MessageUtil messageUtil;

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
	@Order(2)
	public void createOrganization_ReturnsCreated() throws Exception {
		OrganizationDto organizationDto = new OrganizationDto();
		organizationDto.setOrganizationName("Org");
		organizationDto.setCountry("Canada");
		organizationDto.setOrganizationTimeZone("Asia/Kolkata");

		mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(organizationDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Organization created successfully"));
	}

	@Test
	@Order(1)
	public void createOrganizationOnlyWithName_ReturnsUnprocessedEntity() throws Exception {
		OrganizationDto organizationDto = new OrganizationDto();
		organizationDto.setOrganizationName("Org");

		mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(organizationDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value(messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_ERROR)))
			.andExpect(jsonPath("['results'][0]['errors'][0]['field']").value("country"))
			.andExpect(jsonPath("['results'][0]['errors'][0]['message']").value("must not be null"));
	}

	@Test
	@Order(3)
	public void getOrganizationOnlyWithName_ReturnsOk() throws Exception {

		mvc.perform(get(PATH).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['organizationName']").value("Org"))
			.andExpect(jsonPath("['results'][0]['country']").value("Canada"));

	}

	@Test
	@Order(4)
	public void updateOrganizationOnlyWithName_ReturnsOk() throws Exception {
		UpdateOrganizationRequestDto organizationDto = new UpdateOrganizationRequestDto();
		organizationDto.setOrganizationName("NewOrg");

		mvc.perform(patch(PATH).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(organizationDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['organizationName']").value("NewOrg"));
	}

}
