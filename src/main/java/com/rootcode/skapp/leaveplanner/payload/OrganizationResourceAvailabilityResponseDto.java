package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.type.EmployeeAvailabilityStatus;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationResourceAvailabilityResponseDto {

	private LocalDate date;

	private EmployeeAvailabilityStatus availabilityStatus;

	private Integer availableEmployeeCount;

	private List<EmployeeLeaveRequestListResponseDto> employeesOnLeaveRequestResponseDtos;

	private List<HolidayResponseDto> holidayResponseDto;

}
