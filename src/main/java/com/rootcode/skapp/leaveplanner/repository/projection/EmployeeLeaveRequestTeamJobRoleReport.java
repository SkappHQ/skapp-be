package com.rootcode.skapp.leaveplanner.repository.projection;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;

public interface EmployeeLeaveRequestTeamJobRoleReport {

	Long getEmployeeId();

	String getEmployeeName();

	String getTeams();

	String getLeaveType();

	LeaveRequestStatus getStatus();

	String getReason();

	String getLeavePeriod();

	String getDateRequested();

	float getDays();

}
