package com.skapp.community.peopleplanner.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeQuickAddDto {

	@NotNull(message = "{notnull.firstName}")
	private String firstName;

	@NotNull(message = "{notnull.lastName}")
	private String lastName;

	@NotNull(message = "{notnull.workEmail}")
	private String workEmail;

	private RoleRequestDto userRoles;

}
