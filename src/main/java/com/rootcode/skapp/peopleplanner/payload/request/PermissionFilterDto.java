package com.rootcode.skapp.peopleplanner.payload.request;

import com.rootcode.skapp.common.type.EmployeeUserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionFilterDto {

	private String keyword;

	private EmployeeUserRole userRole;

}
