package com.rootcode.skapp.common.payload.response;

import com.rootcode.skapp.peopleplanner.payload.response.EmployeeCredentialsResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharePasswordResponseDto {

	private Long userId;

	private EmployeeCredentialsResponseDto employeeCredentials;

	private String firstName;

	private String lastName;

}
