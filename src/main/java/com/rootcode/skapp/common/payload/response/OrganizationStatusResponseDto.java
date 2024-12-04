package com.rootcode.skapp.common.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationStatusResponseDto {

	private Boolean isOrganizationSetupCompleted;

	private Boolean isSignUpCompleted;

}
