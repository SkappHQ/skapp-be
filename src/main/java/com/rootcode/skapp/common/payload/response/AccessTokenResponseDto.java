package com.rootcode.skapp.common.payload.response;

import com.rootcode.skapp.peopleplanner.payload.response.EmployeeResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenResponseDto {

	private String accessToken;

	private EmployeeResponseDto employee;

}
