package com.skapp.community.peopleplanner.payload.response;

import com.skapp.community.peopleplanner.payload.request.JobTitleDto;
import com.skapp.community.peopleplanner.type.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeProgressionResponseDto {

	private Long progressionId;

	private EmployeeType employeeType;

	private JobTitleDto jobTitle;

	private EmployeeJobFamilyDto jobFamily;

	private LocalDate startDate;

	private LocalDate endDate;

}
