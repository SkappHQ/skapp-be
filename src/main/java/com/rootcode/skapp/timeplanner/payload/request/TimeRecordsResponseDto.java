package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.peopleplanner.payload.response.EmployeeTeamResponseDto;
import com.rootcode.skapp.timeplanner.payload.response.TimeRecordChipResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimeRecordsResponseDto {

	private EmployeeTeamResponseDto employee;

	private List<TimeRecordChipResponseDto> timeRecords;

}
