package com.skapp.community.common.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.payload.request.SignInRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	private final String PATH = "/v1/auth";

	@Test
	public void signin_ReturnsOk() throws Exception {
		SignInRequestDto signInRequestDto = new SignInRequestDto();
		signInRequestDto.setEmail("user1@gmail.com");
		signInRequestDto.setPassword("Test@123");

		mvc.perform(post(PATH.concat("/sign-in")).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signInRequestDto))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("['status']").value("successful"))
			.andExpect(jsonPath("['results'][0]['accessToken']").exists());
	}

}
