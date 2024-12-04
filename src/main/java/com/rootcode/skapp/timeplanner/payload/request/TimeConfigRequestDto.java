package com.rootcode.skapp.timeplanner.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TimeConfigRequestDto {

	private Boolean isClockInOnNonWorkingDays;

	private Boolean isClockInOnCompanyHolidays;

	private Boolean isClockInOnLeaveDays;

	private Boolean isAutoApprovalForChanges;

}
