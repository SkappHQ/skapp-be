package com.rootcode.skapp.leaveplanner.payload.response;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;
import com.rootcode.skapp.leaveplanner.type.LeaveState;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LeaveRequestManagerResponseDto {

	private EmployeeBasicDetailsResponseDto employee;

	private Long leaveRequestId;

	private String startDate;

	private String endDate;

	private LeaveTypeBasicDetailsResponseDto leaveType;

	private LeaveState leaveState;

	private LeaveRequestStatus status;

	private Integer durationHours;

	private Float durationDays;

	private LocalDateTime createdDate;

	private String requestDesc;

}
