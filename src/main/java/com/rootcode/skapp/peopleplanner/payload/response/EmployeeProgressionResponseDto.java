package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.JobTitleDto;
import com.rootcode.skapp.peopleplanner.type.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmployeeProgressionResponseDto {

	private Long progressionId;

	private EmployeeType employeeType;

	private JobTitleDto jobTitle;

	private EmployeeJobFamilyDto jobFamily;

	private Date startDate;

	private Date endDate;

}
