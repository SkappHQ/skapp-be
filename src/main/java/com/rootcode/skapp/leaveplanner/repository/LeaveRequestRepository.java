package com.rootcode.skapp.leaveplanner.repository;

import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.leaveplanner.model.LeaveRequest;
import com.rootcode.skapp.leaveplanner.payload.EmployeeLeaveHistoryFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveRequestFilterDto;
import com.rootcode.skapp.leaveplanner.payload.TeamLeaveHistoryFilterDto;
import com.rootcode.skapp.leaveplanner.payload.request.EmployeesOnLeavePeriodFilterDto;
import com.rootcode.skapp.leaveplanner.payload.response.EmployeeLeaveRequestReportExportDto;
import com.rootcode.skapp.leaveplanner.payload.response.EmployeeLeaveRequestReportQueryDto;
import com.rootcode.skapp.timeplanner.model.TimeConfig;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository {

	List<LeaveRequest> findLeaveRequestsByDateRange(LeaveRequestFilterDto leaveRequestFilterDto, Long employeeId);

	List<LeaveRequest> findAllLeaveRequestsByDateRange(LeaveRequestFilterDto leaveRequestFilterDto);

	List<LeaveRequest> findLeaveRequestsForTodayByUser(LocalDate currentDate, Long employeeId);

	List<LeaveRequest> findLeaveRequestAvailabilityForGivenDate(LocalDate date, Long employeeId);

	List<LeaveRequest> findLeaveRequestsByDateRangeAndEmployees(LeaveRequestFilterDto leaveRequestFilterDto,
			List<Long> employeeIds);

	List<LeaveRequest> getLeaveRequestsByTeamId(EmployeesOnLeavePeriodFilterDto leaveRequestFilterDto);

	Float findAllEmployeeAnnualDaysByDateRangeQuery(Long typeId, LocalDate firstDateOfYear, LocalDate currentDate);

	Page<LeaveRequest> getLeaveRequestHistoryByTeam(Long id, TeamLeaveHistoryFilterDto teamLeaveHistoryFilterDto,
			Pageable pageable);

	Page<LeaveRequest> findAllLeaveRequestsByEmployeeId(@NonNull Long id,
			EmployeeLeaveHistoryFilterDto employeeLeaveHistoryFilterDto, Pageable pageable);

	List<LeaveRequest> findAllFutureLeaveRequestsForTheDay(DayOfWeek day);

	List<LeaveRequest> findRequestsByDateRangeAndEmployee(@NonNull Long employeeId,
			LeaveRequestFilterDto leaveRequestFilterDto);

	Optional<LeaveRequest> findAuthLeaveRequestById(Long id, User user, Boolean isManager);

	Page<LeaveRequest> findAllRequestsByEmployee(Long employeeId, LeaveRequestFilterDto leaveRequestFilterDto,
			Pageable page);

	LeaveRequest findByEmployeeAndDate(Long employeeId, LocalDate date);

	Page<LeaveRequest> findAllLeaveRequests(Long managerEmployeeId, LeaveRequestFilterDto leaveRequestFilterDto,
			boolean isLeaveAdmin, Pageable page);

	Page<LeaveRequest> findAllRequestAssignedToManager(Long employeeId, LeaveRequestFilterDto leaveRequestFilterDto,
			Pageable pageable);

	List<LeaveRequest> findPendingLeaveRequestsByManager(Long employeeId, String searchKeyword);

	List<LeaveRequest> getEmployeesOnLeaveByTeamAndDate(List<Long> teams, LocalDate current, Long currentUserId,
			boolean isLeaveAdmin);

	List<EmployeeLeaveRequestReportExportDto> generateLeaveRequestDetailedReport(List<Long> leaveTypeIds,
			LocalDate startDate, LocalDate endDate, Long jobFamilyId, Long teamId, List<String> statuses);

	Page<EmployeeLeaveRequestReportQueryDto> generateLeaveRequestDetailedReportWithPagination(List<Long> leaveTypeIds,
			LocalDate startDate, LocalDate endDate, Long jobFamilyId, Long teamId, List<String> statuses,
			Pageable pageable);

	Float findAllEmployeeRequestsByWithinThirtyDays(LocalDate startDate, LocalDate endDate,

			List<TimeConfig> timeConfigs, List<LocalDate> holidayDates, List<Long> teamIds);

}
