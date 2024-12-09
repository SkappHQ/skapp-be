package com.skapp.community.leaveplanner.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeaveRequestReportExportDto {

	private Long employeeId;

	private String employeeName;

	private String teams;

	private String leaveType;

	private String status;

	private Float durationDays;

	private String leavePeriod;

	private String dateRequested;

	private float days;

	private String reason;

}
