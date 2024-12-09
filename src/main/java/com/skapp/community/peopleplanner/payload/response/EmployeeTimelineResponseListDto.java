package com.skapp.community.peopleplanner.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeTimelineResponseListDto {

	private Long year;

	private String month;

	List<EmployeeTimelineResponseDto> employeeTimelineRecords;

}
