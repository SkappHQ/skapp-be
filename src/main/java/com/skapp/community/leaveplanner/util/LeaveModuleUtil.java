package com.skapp.community.leaveplanner.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skapp.community.common.exception.EntityNotFoundException;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.type.Role;
import com.skapp.community.common.util.CommonModuleUtils;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.leaveplanner.constant.LeaveMessageConstant;
import com.skapp.community.leaveplanner.model.LeaveRequest;
import com.skapp.community.peopleplanner.constant.PeopleMessageConstant;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.model.Holiday;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.timeplanner.model.TimeConfig;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@UtilityClass
public class LeaveModuleUtil {

	public static int getLeaveCycleEndYear(int cycleStartMonth, int cycleStartDay) {
		LocalDate currentDate = DateTimeUtils.getCurrentUtcDate();
		int currentYear = currentDate.getYear();
		int currentMonth = currentDate.getMonthValue();

		int cycleEndYear;

		if (cycleStartMonth == 1) { // January
			if (cycleStartDay == 1) {
				cycleEndYear = currentYear;
			}
			else {
				cycleEndYear = currentYear + 1;
			}
		}
		else {
			if (currentMonth < cycleStartMonth) {
				cycleEndYear = currentYear;
			}
			else {
				cycleEndYear = currentYear + 1;
			}
		}

		return cycleEndYear;
	}

	public static ObjectNode getLeaveCycleConfigs(String leaveCycleData) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (ObjectNode) mapper.readTree(leaveCycleData);
		}
		catch (Exception e) {
			throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_PARSING_LEAVE_CYCLE_DATA,
					new String[] { e.getMessage() });
		}
	}

	public static LocalDate getDateFromTheGivenBusinessDayCount(LocalDate localDate, int businessDayCount,
			List<Integer> workingDaysIndex, List<LocalDate> holidayDates) {
		LocalDate currentDate = LocalDate.from(localDate); // from day before given date
		int count = 1; // including current day

		while (count < businessDayCount) {
			currentDate = currentDate.minusDays(1);

			// Check if the current date is a working day
			if ((workingDaysIndex.isEmpty() || workingDaysIndex.contains(currentDate.getDayOfWeek().getValue() - 1))
					&& !holidayDates.contains(currentDate)) {
				count++;
			}
		}
		return currentDate;
	}

	public static int getNumberOfMonthsBetweenTwoDates(LocalDate startDate, LocalDate endDate) {
		return (int) ChronoUnit.MONTHS.between(startDate, endDate) + 1;
	}

	public static int getNumberOfDaysBetweenLeaveRequestForGivenEntitlementRange(LocalDate leaveRequestStartDate,
			LocalDate leaveRequestEndDate, LocalDate entitlementValidFrom, LocalDate entitlementValidTo,
			List<TimeConfig> timeConfigs, List<LocalDate> holidays) {
		return getNumberOfDaysBetweenLeaveRequestForGivenEntitlementRange(leaveRequestStartDate, leaveRequestEndDate,
				entitlementValidFrom, entitlementValidTo, timeConfigs, holidays, null, null);
	}

	public static int getNumberOfDaysBetweenLeaveRequestForGivenEntitlementRange(LocalDate leaveRequestStartDate,
			LocalDate leaveRequestEndDate, LocalDate entitlementValidFrom, LocalDate entitlementValidTo,
			List<TimeConfig> timeConfigs, List<LocalDate> holidays, List<Holiday> holidayObjects,
			LeaveRequest leaveRequest) {
		// Adjust dates if necessary
		LocalDate startDate = leaveRequestStartDate.isAfter(leaveRequestEndDate) ? leaveRequestEndDate
				: leaveRequestStartDate;
		LocalDate endDate = leaveRequestEndDate.isBefore(leaveRequestStartDate) ? leaveRequestStartDate
				: leaveRequestEndDate;
		LocalDate entitlementStart = entitlementValidFrom.isAfter(entitlementValidTo) ? entitlementValidTo
				: entitlementValidFrom;
		LocalDate entitlementEnd = entitlementValidTo.isBefore(entitlementValidFrom) ? entitlementValidFrom
				: entitlementValidTo;

		// Calculate the overlapping date range
		LocalDate overlapStart = startDate.isAfter(entitlementStart) ? startDate : entitlementStart;
		LocalDate overlapEnd = endDate.isBefore(entitlementEnd) ? endDate : entitlementEnd;

		int count = 0;

		// Iterate through the overlapping date range
		LocalDate currentDate = overlapStart;
		while (!currentDate.isAfter(overlapEnd)) {
			if (CommonModuleUtils.checkIfDayIsWorkingDay(currentDate, timeConfigs)
					&& CommonModuleUtils.checkIfDayIsNotAHoliday(leaveRequest, holidayObjects, holidays, currentDate)) {
				count++;
			}
			currentDate = currentDate.plusDays(1);
		}
		return count;
	}

	public static boolean isHolidayContainsBetweenTwoDates(LocalDate startDate, LocalDate endDate,
			List<LocalDate> holidays, List<Holiday> holidayObjects, LeaveRequest leaveRequest) {
		if (startDate.isAfter(endDate)) {
			LocalDate temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			if (!CommonModuleUtils.checkIfDayIsNotAHoliday(leaveRequest, holidayObjects, holidays, currentDate)) {
				return true;
			}
			currentDate = currentDate.plusDays(1);
		}

		return false;
	}

	public static int getWorkingDaysBetweenTwoDates(LocalDate startDate, LocalDate endDate,
			List<TimeConfig> timeConfigs, List<LocalDate> holidays, List<Holiday> holidayObjects,
			LeaveRequest leaveRequest) {
		if (startDate.isAfter(endDate)) {
			LocalDate temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		int workDays = 0;
		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			if (CommonModuleUtils.checkIfDayIsWorkingDay(currentDate, timeConfigs)
					&& CommonModuleUtils.checkIfDayIsNotAHoliday(leaveRequest, holidayObjects, holidays, currentDate)) {
				workDays++;
			}
			currentDate = currentDate.plusDays(1);
		}
		return workDays;
	}

	public static void validateTeamsForLeaveAnalytics(List<Long> teamIds, User currentUser, List<Team> teams) {
		if (teamIds == null || (teamIds.size() == 1 && teamIds.contains(-1L))) {
			return;
		}
		boolean isSuperAdminOrAttendanceAdmin = isUserSuperAdminOrLeaveAdmin(currentUser);

		validateTeamsExist(teamIds, teams);
		if (!isSuperAdminOrAttendanceAdmin) {
			validateUserIsSupervisor(teams, currentUser);
		}
	}

	public static boolean isUserSuperAdminOrLeaveAdmin(User user) {
		EmployeeRole role = user.getEmployee().getEmployeeRole();
		return role.getIsSuperAdmin() || Role.LEAVE_ADMIN.equals(role.getAttendanceRole());
	}

	public static void validateTeamsExist(List<Long> teamIds, List<Team> teams) {
		List<Long> unavailableTeams = teamIds.stream()
			.filter(teamId -> teams.stream().noneMatch(t -> t.getTeamId().equals(teamId)))
			.toList();
		if (!unavailableTeams.isEmpty()) {
			throw new EntityNotFoundException(PeopleMessageConstant.PEOPLE_ERROR_TEAM_NOT_FOUND,
					new String[] { unavailableTeams.toString() });
		}
	}

	public static void validateUserIsSupervisor(List<Team> teams, User user) {
		List<Long> notSupervisingTeams = teams.stream()
			.filter(team -> team.getEmployees()
				.stream()
				.noneMatch(emp -> emp.getEmployee().getEmployeeId().equals(user.getEmployee().getEmployeeId())
						&& emp.getIsSupervisor()))
			.map(Team::getTeamId)
			.toList();
		if (!notSupervisingTeams.isEmpty()) {
			throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_USER_IS_NOT_SUPERVISOR_FOR_SELECTED_TEAMS,
					new String[] { notSupervisingTeams.toString() });
		}
	}

}
