package com.skapp.community.peopleplanner.payload.response;

import com.skapp.community.leaveplanner.type.ManagerType;
import com.skapp.community.peopleplanner.payload.request.employee.employment.EmployeeEmploymentBasicDetailsDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerResponseDto {

	private EmployeeEmploymentBasicDetailsDto manager;

	private Boolean isPrimaryManager;

	private ManagerType managerType;

}
