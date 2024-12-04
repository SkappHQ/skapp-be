package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.payload.response.SummarizedLeaveTypeDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SummarizedCustomLeaveEntitlementDto {

	private long entitlementId;

	private Float totalDaysAllocated;

	private Float totalDaysUsed;

	private LocalDate validFrom;

	private LocalDate validTo;

	private SummarizedLeaveTypeDto leaveType;

	private EmployeeBasicDetailsResponseDto employee;

}
