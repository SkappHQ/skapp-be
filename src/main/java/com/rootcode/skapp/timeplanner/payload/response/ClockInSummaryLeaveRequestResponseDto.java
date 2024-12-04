package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;
import com.rootcode.skapp.leaveplanner.type.LeaveState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClockInSummaryLeaveRequestResponseDto {

	private Long leaveRequestId;

	private LeaveState leaveState;

	private LeaveRequestStatus status;

	private ClockInSummaryLeaveTypeResponseDto leaveType;

}
