package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TimeRequestAvailabilityResponseDto {

	private LocalDate date;

	private Boolean timeSlotsExists;

	private List<EmployeeTimeRequestResponseDto> manualEntryRequests;

	private EmployeeTimeRequestResponseDto editTimeRequests;

	private List<LeaveRequestResponseDto> leaveRequest;

	private List<HolidayResponseDto> holiday;

}
