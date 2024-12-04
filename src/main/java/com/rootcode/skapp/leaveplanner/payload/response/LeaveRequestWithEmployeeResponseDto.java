package com.rootcode.skapp.leaveplanner.payload.response;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;
import com.rootcode.skapp.leaveplanner.type.LeaveState;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestWithEmployeeResponseDto {

	private Long leaveRequestId;

	private LeaveState leaveState;

	private LeaveRequestStatus status;

	private LocalDate startDate;

	private LocalDate endDate;

	private LeaveTypeBasicDetailsResponseDto leaveType;

	private EmployeeBasicDetailsResponseDto employee;

	private EmployeeBasicDetailsResponseDto reviewer;

}
