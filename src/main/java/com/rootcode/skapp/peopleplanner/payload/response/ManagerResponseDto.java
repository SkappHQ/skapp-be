package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.leaveplanner.type.ManagerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerResponseDto {

	private EmployeeResponseDto manager;

	private Boolean isPrimaryManager;

	private ManagerType managerType;

}
