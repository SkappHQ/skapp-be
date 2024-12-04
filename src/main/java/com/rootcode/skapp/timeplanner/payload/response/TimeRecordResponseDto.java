package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.response.EmployeeTeamResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TimeRecordResponseDto {

	private EmployeeTeamResponseDto employee;

	private List<TimeRecordChipResponseDto> timeRecords;

}
