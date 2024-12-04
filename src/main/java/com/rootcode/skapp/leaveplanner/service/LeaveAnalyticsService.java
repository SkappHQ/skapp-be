package com.rootcode.skapp.leaveplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeLeaveHistoryFilterDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeesOnLeaveFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementEmployeeDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementsFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveRequestFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveTrendFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveUtilizationFilterDto;
import com.rootcode.skapp.leaveplanner.payload.ManagerLeaveTrendFilterDto;
import com.rootcode.skapp.leaveplanner.payload.ManagerTeamResourceAvailabilityDto;
import com.rootcode.skapp.leaveplanner.payload.OrganizationLeaveTrendForTheYearFilterDto;
import com.rootcode.skapp.leaveplanner.payload.TeamFilterDto;
import com.rootcode.skapp.leaveplanner.payload.TeamLeaveHistoryFilterDto;
import com.rootcode.skapp.leaveplanner.payload.TeamLeaveTrendForTheYearFilterDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeFilterDto;
import jakarta.validation.Valid;
import lombok.NonNull;

import java.util.List;

public interface LeaveAnalyticsService {

	ResponseEntityDto getLeaveTrends(LeaveTrendFilterDto leaveTrendFilterDto);

	ResponseEntityDto getLeaveTypeBreakdown(List<Long> typeIds, List<Long> teamIds);

	ResponseEntityDto getEmployeesOnLeave(EmployeesOnLeaveFilterDto employeesOnLeaveFilterDto);

	ResponseEntityDto getEmployeeLeaveUtilization(LeaveUtilizationFilterDto leaveUtilizationFilterDto);

	ResponseEntityDto getOrganizationLeaveTrendForTheYear(
			OrganizationLeaveTrendForTheYearFilterDto organizationLeaveTrendForTheYearFilterDto);

	ResponseEntityDto getManagerLeaveTrend(ManagerLeaveTrendFilterDto managerLeaveTrendFilterDto);

	ResponseEntityDto getTeamLeaveHistory(Long id, TeamLeaveHistoryFilterDto teamLeaveHistoryFilterDto);

	ResponseEntityDto getTeamLeaveSummary(Long id);

	ResponseEntityDto getEmployeeLeaveHistory(@NonNull Long id,
			EmployeeLeaveHistoryFilterDto employeeLeaveHistoryFilterDto);

	ResponseEntityDto getTeamsByTeamLead(TeamFilterDto teamFilterDto);

	ResponseEntityDto getIndividualsByManager(EmployeeFilterDto employeeFilterDto);

	ResponseEntityDto getEmployeeLeaveEntitlements(@NonNull Long id,
			LeaveEntitlementsFilterDto leaveEntitlementsFilterDto);

	ResponseEntityDto getOrganizationalLeaveAnalytics();

	ResponseEntityDto getOrganizationalAbsenceRate(List<Long> teamIds);

	ResponseEntityDto getTeamLeaveTrendForTheYear(TeamLeaveTrendForTheYearFilterDto teamLeaveTrendForTheYearFilterDto);

	ResponseEntityDto getTeamResourceAvailability(
			ManagerTeamResourceAvailabilityDto managerTeamResourceAvailabilityDto);

	ResponseEntityDto getEntitlementsByLeaveTypeJobRoleTeam(LeaveEntitlementEmployeeDto leaveEntitlementEmployeeDto);

	ResponseEntityDto getLeaveReportFile(LeaveEntitlementEmployeeDto leaveEntitlementEmployeeDto);

	ResponseEntityDto getEmployeesOnLeaveByTeam(@Valid EmployeesOnLeaveFilterDto employeesOnLeaveFilterDto);

	ResponseEntityDto getLeaves(LeaveRequestFilterDto leaveRequestFilterDto);

	ResponseEntityDto getCustomEntitlementsByLeaveTypeJobRoleTeam(
			LeaveEntitlementEmployeeDto leaveEntitlementEmployeeDto);

	ResponseEntityDto getLeaveRequestsByLeaveTypeJobRoleTeam(LeaveEntitlementEmployeeDto leaveEntitlementEmployeeDto);

}
