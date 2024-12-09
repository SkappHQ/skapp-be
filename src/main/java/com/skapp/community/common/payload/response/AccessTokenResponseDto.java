package com.skapp.community.common.payload.response;

import com.skapp.community.peopleplanner.payload.response.EmployeeResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenResponseDto {

	private String accessToken;

	private EmployeeResponseDto employee;

}
