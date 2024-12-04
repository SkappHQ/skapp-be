package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.timeplanner.payload.request.TimeRecordsResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamTimeRecordSummaryResponseDto {

	TimeRecordsSummary timeRecordsSummary;

	List<TimeRecordsResponseDto> employeeTimeRecordSummaries;

}
