package com.rootcode.skapp.leaveplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveRequestFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveRequestManagerUpdateDto;
import com.rootcode.skapp.leaveplanner.payload.ResourceAvailabilityCalendarFilter;
import com.rootcode.skapp.leaveplanner.payload.request.LeavePatchRequestDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveRequestAvailabilityFilterDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveRequestDto;
import com.rootcode.skapp.leaveplanner.payload.request.PendingLeaveRequestFilterDto;
import com.rootcode.skapp.leaveplanner.payload.response.LeaveNotificationNudgeResponseDto;
import jakarta.validation.Valid;

public interface LeaveService {

	ResponseEntityDto applyLeaveRequest(LeaveRequestDto leaveRequestDTO);

	ResponseEntityDto updateLeaveRequestByManager(Long id, LeaveRequestManagerUpdateDto leaveRequestManagerUpdateDto,
			boolean isInvokedByManager);

	ResponseEntityDto updateLeaveRequestByEmployee(LeavePatchRequestDto leavePatchRequestDto, Long id);

	ResponseEntityDto getCurrentUserLeaveRequests(LeaveRequestFilterDto leaveRequestFilterDto);

	ResponseEntityDto getLeaveRequestById(Long id);

	ResponseEntityDto getAssignedLeaveRequestById(Long id);

	ResponseEntityDto deleteLeaveRequestById(Long id);

	ResponseEntityDto getAssignedLeavesToManager(LeaveRequestFilterDto leaveRequestFilterDto);

	ResponseEntityDto getAssignedPendingLeavesToManager(PendingLeaveRequestFilterDto pendingLeaveRequestFilterDto);

	ResponseEntityDto getResourceAvailabilityCalendar(
			ResourceAvailabilityCalendarFilter resourceAvailabilityCalendarFilter);

	ResponseEntityDto nudgeManagers(@Valid Long leaveRequestId);

	ResponseEntityDto leaveRequestAvailability(@Valid LeaveRequestAvailabilityFilterDto requestAvailabilityDto);

	LeaveNotificationNudgeResponseDto getLeaveRequestIsNudge(@Valid Long leaveRequestId);

}
