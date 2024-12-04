package com.rootcode.skapp.timeplanner.service;

import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeTimeRequestFilterDto;
import com.rootcode.skapp.peopleplanner.payload.request.ManagerEmployeeLogFilterDto;
import com.rootcode.skapp.timeplanner.model.TimeConfig;
import com.rootcode.skapp.timeplanner.model.TimeRequest;
import com.rootcode.skapp.timeplanner.payload.request.AddTimeRecordDto;
import com.rootcode.skapp.timeplanner.payload.request.EditTimeRequestDto;
import com.rootcode.skapp.timeplanner.payload.request.EmployeeAttendanceSummaryFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.IndividualWorkHourFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.ManagerAttendanceSummaryFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.ManagerTimeRecordFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.ManagerTimeRequestFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.ManualEntryRequestDto;
import com.rootcode.skapp.timeplanner.payload.request.TeamTimeRecordFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.TimeConfigDto;
import com.rootcode.skapp.timeplanner.payload.request.TimeRecordFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.TimeRequestAvailabilityRequestDto;
import com.rootcode.skapp.timeplanner.payload.request.TimeRequestManagerPatchDto;
import com.rootcode.skapp.timeplanner.payload.request.UpdateIncompleteTimeRecordsRequestDto;
import com.rootcode.skapp.timeplanner.payload.request.UpdateTimeRequestsFilterDto;
import com.rootcode.skapp.timeplanner.payload.response.UtilizationPercentageDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface TimeService {

	ResponseEntityDto updateTimeConfigs(TimeConfigDto timeConfigDto);

	ResponseEntityDto getActiveTimeSlot();

	ResponseEntityDto getEmployeeAttendanceSummary(
			EmployeeAttendanceSummaryFilterDto employeeAttendanceSummaryFilterDto);

	ResponseEntityDto getEmployeeDailyTimeRecords(TimeRecordFilterDto timeRecordFilterDto);

	ResponseEntityDto getAllRequestsOfEmployee(EmployeeTimeRequestFilterDto employeeTimeRequestFilterDto);

	ResponseEntityDto getRequestedDateTimeAvailability(TimeRequestAvailabilityRequestDto requestDto);

	ResponseEntityDto getIncompleteClockOuts();

	ResponseEntityDto getDefaultTimeConfigurations();

	ResponseEntityDto addManualEntryRequest(ManualEntryRequestDto timeRequestDto);

	ResponseEntityDto updateTimeRequests(UpdateTimeRequestsFilterDto updateTimeRequestsFilterDto);

	ResponseEntityDto updateCurrentUserIncompleteTimeRecords(Long id, UpdateIncompleteTimeRecordsRequestDto requestDto);

	ResponseEntityDto addTimeRecord(AddTimeRecordDto addTimeRecordDto);

	@Transactional
	ResponseEntityDto editTimeRequest(EditTimeRequestDto timeRequestDto);

	ResponseEntityDto getManagerAttendanceSummary(ManagerAttendanceSummaryFilterDto managerAttendanceSummaryFilterDto);

	ResponseEntityDto updateTimeRequestByManager(Long id, TimeRequestManagerPatchDto timeRequestManagerPatchDto);

	TimeRequest handleEditTimeRecordRequests(TimeRequest timeRequest, User currentUser,
			TimeRequestManagerPatchDto timeRequestManagerPatchDto);

	TimeRequest handleManualTimeEntryRequests(TimeRequest timeRequest, User currentUser,
			TimeRequestManagerPatchDto timeRequestManagerPatchDto);

	ResponseEntityDto managerAssignUsersTimeRecords(ManagerTimeRecordFilterDto managerTimeRecordFilterDto);

	ResponseEntityDto getAllAssignEmployeesTimeRequests(ManagerTimeRequestFilterDto timeRequestFilterDto);

	ResponseEntityDto managerTeamTimeRecordSummary(TeamTimeRecordFilterDto timeRecordSummaryDto);

	ResponseEntityDto getManagerEmployeeDailyLog(ManagerEmployeeLogFilterDto managerEmployeeLogFilterDto);

	ResponseEntityDto getIndividualWorkHoursBySupervisor(IndividualWorkHourFilterDto individualWorkHourFilterDto);

	ResponseEntityDto getIndividualWorkUtilizationByManager(Long id);

	void getDefaultTimeConfigs();

	ResponseEntityDto getIfTimeConfigRemovable(List<DayOfWeek> days);

	UtilizationPercentageDto calculateWorkTimeUtilization(List<Long> employeeId, List<TimeConfig> timeConfigs,
			List<LocalDate> holidays);

	ResponseEntityDto getEmployeeDailyTimeRecordsByEmployeeId(TimeRecordFilterDto timeRecordFilterDto, Long employeeId);

}
