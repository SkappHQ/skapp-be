package com.rootcode.skapp.leaveplanner.repository.projection;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;

import java.time.LocalDate;

public interface EmployeeLeaveRequestTeamJobRoleReportResponseDto {

	Long getEmployeeId();

	String getAuthPic();

	String getFirstName();

	String getLastName();

	String getTeams();

	String getLeaveType();

	LeaveRequestStatus getStatus();

	LocalDate getStartDate();

	LocalDate getEndDate();

	String getLeaveTypeEmoji();

	float getDays();

}
