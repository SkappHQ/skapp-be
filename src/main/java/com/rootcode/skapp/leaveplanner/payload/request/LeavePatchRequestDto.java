package com.rootcode.skapp.leaveplanner.payload.request;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeavePatchRequestDto {

	private LocalDate startDate;

	private LocalDate endDate;

	private Boolean isViewed;

	private String requestDesc;

	private LeaveRequestStatus leaveRequestStatus;

}
