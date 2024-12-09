package com.skapp.community.peopleplanner.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.model.User;
import com.skapp.community.common.type.Role;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.payload.request.JobFamilyDto;
import com.skapp.community.peopleplanner.payload.request.JobTitleDto;
import com.skapp.community.peopleplanner.payload.request.TransferJobTitleRequestDto;
import com.skapp.community.peopleplanner.payload.request.UpdateJobFamilyRequestDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class JobControllerIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String path = "/v1/job";

	@BeforeEach
	public void setup() {

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
	void createJobFamily_ReturnsCreated() throws Exception {
		JobFamilyDto jobFamilyDto = new JobFamilyDto();
		jobFamilyDto.setName("Engineer");
		jobFamilyDto.setTitles(Stream.of("Senior", "Junior").toList());
		mvc.perform(post(path.concat("/family")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(jobFamilyDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("['status']").value("successful"));
	}

	@Test
	void getJobFamily_ReturnsOk() throws Exception {

		mvc.perform(get(path.concat("/family/2")).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"));
	}

	@Test
	void getJobFamilyWithNotExistingId_ReturnsNotFound() throws Exception {

		mvc.perform(get(path.concat("/family/12")).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Job family isn't found"));
	}

	@Test
	void createJobFamilyWithEmptyTitles_ReturnsBadRequest() throws Exception {
		JobFamilyDto jobFamilyDto = new JobFamilyDto();
		jobFamilyDto.setName("Engineer");
		List<String> jobTitleDtoList = new ArrayList<>();
		jobFamilyDto.setTitles(jobTitleDtoList);
		mvc.perform(post(path.concat("/family")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(jobFamilyDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Insufficient data for job family"));
	}

	@Test
	void getAllJobFamily_withValidEmployee_returnsHttpStatusOk() throws Exception {
		mvc.perform(get(path.concat("/family")).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['results'][0]['jobFamilyId']").value(1))
			.andExpect(jsonPath("['results'][0]['name']").value("Software Engineer"))
			.andExpect(jsonPath("['results'][1]['jobFamilyId']").value(2))
			.andExpect(jsonPath("['results'][1]['name']").value("Business Analyst"))
			.andExpect(jsonPath("['results'][2]['jobFamilyId']").value(5))
			.andExpect(jsonPath("['results'][2]['name']").value("Resource Manager"));
	}

	@Test
	void addJobFamily_invalidJobRoleLevel_returnsHttpStatusBadRequest() throws Exception {
		JobFamilyDto jobFamilyDto = new JobFamilyDto();
		jobFamilyDto.setName("Engineer");
		jobFamilyDto.setTitles(Stream.of("Lead#1", "Junior").toList());

		mvc.perform(post(path.concat("/family")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(jobFamilyDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value(
					"Job family name & job title fields can only contain alphabets, numbers, whitespaces, and following symbols -_&/|[]"))
			.andReturn();

	}

	@Test
	void updateJobFamily_returnsHttpStatusCreated() throws Exception {
		UpdateJobFamilyRequestDto updateJobFamilyRequestDto = new UpdateJobFamilyRequestDto();
		updateJobFamilyRequestDto.setName("Consultation");
		JobTitleDto jobTitleDto = new JobTitleDto();
		jobTitleDto.setJobTitleId(1L);
		jobTitleDto.setName("trainee");
		List<JobTitleDto> jobTitleDtoList = new ArrayList<>();
		jobTitleDtoList.add(jobTitleDto);
		updateJobFamilyRequestDto.setTitles(jobTitleDtoList);

		mvc.perform(patch(path.concat("/family/1")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateJobFamilyRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['name']").value("Consultation"))
			.andReturn();
	}

	@Test
	void getInvalid_JobTitleById_returnsHttpStatusOk() throws Exception {
		mvc.perform(get(path.concat("/title/10")).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andReturn();
	}

	@Test
	void getJobTitleById_returnsHttpStatusOk() throws Exception {
		mvc.perform(get(path.concat("/title/2")).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['name']").value("Senior"))
			.andReturn();
	}

	@Test
	void deleteJobTitle_withInvalidJobTitle_returnsHttpStatusNotFound() throws Exception {
		List<TransferJobTitleRequestDto> transferJobTitleDtos = new ArrayList<>();
		TransferJobTitleRequestDto transferJobLevelDto = new TransferJobTitleRequestDto();
		transferJobLevelDto.setJobTitleId(2L);
		transferJobLevelDto.setEmployeeId(3L);
		transferJobTitleDtos.add(transferJobLevelDto);
		mvc.perform(patch(path.concat("/title/transfer/10")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(transferJobTitleDtos))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']").value("Job title isn't found"));
	}

	@Test
	void deleteJobTitle_withTransferring_returnsHttpStatusOk() throws Exception {
		List<TransferJobTitleRequestDto> transferJobTitleDtos = new ArrayList<>();
		TransferJobTitleRequestDto transferJobLevelDto = new TransferJobTitleRequestDto();
		transferJobLevelDto.setJobTitleId(4L);
		transferJobLevelDto.setEmployeeId(3L);
		transferJobTitleDtos.add(transferJobLevelDto);
		mvc.perform(patch(path.concat("/title/transfer/5")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(transferJobTitleDtos))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value("Successfully transferred employees to another job title in the same job family"));
	}

	@Test
	void deleteJobTitle_withNotMatchingTitle_returnsHttpStatusBadRequest() throws Exception {
		List<TransferJobTitleRequestDto> transferJobTitleDtos = new ArrayList<>();
		TransferJobTitleRequestDto transferJobLevelDto = new TransferJobTitleRequestDto();
		transferJobLevelDto.setJobTitleId(1L);
		transferJobLevelDto.setEmployeeId(3L);
		transferJobTitleDtos.add(transferJobLevelDto);

		mvc.perform(patch(path.concat("/title/transfer/5")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(transferJobTitleDtos))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value("Job title and job family do not match or invalid job title"));
	}

	@Test
	void deleteJobTitle_withoutTransferringAllEmployees_returnsHttpStatusBadRequest() throws Exception {
		List<TransferJobTitleRequestDto> transferJobTitleDtos = new ArrayList<>();

		mvc.perform(patch(path.concat("/title/transfer/5")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(transferJobTitleDtos))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("['status']").value("unsuccessful"))
			.andExpect(jsonPath("['results'][0]['message']")
				.value("No job title transfer data provided. Unable to process the transfer request."));
	}

}
