package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.JobTitleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobFamilyResponseDetailDto {

	private Long jobFamilyId;

	private String name;

	private List<JobTitleDto> jobTitles;

	private List<EmployeeJobFamilyResponseDto> employees;

}
