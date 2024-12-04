package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.leaveplanner.type.ManagerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeManagerResponseDto {

	private Long employeeId;

	private String firstName;

	private String lastName;

	private String middleName;

	private String authPic;

	private Boolean isPrimaryManager;

	private ManagerType managerType;

}
