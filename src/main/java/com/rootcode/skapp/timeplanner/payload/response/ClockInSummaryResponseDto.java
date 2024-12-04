package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClockInSummaryResponseDto {

	private EmployeeBasicDetailsResponseDto employee;

	private Long timeRecordId;

	private String clockInTime;

	private String clockOutTime;

	private String workedHours;

	private ClockInSummaryLeaveRequestResponseDto leave;

	private HolidayResponseDto holiday;

	private Boolean isLateArrival;

}
