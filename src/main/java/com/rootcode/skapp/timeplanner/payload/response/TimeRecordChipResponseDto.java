package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TimeRecordChipResponseDto {

	private Long timeRecordId;

	private LocalDate date;

	private Float workedHours;

	private LeaveRequestResponseDto leaveRequest;

}
