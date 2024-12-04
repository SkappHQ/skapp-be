package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdminEmployeesOnLeaveDto {

	private AdminOnLeaveDto adminOnLeaveDto;

	private Boolean isNonWorkingDay;

	private List<HolidayResponseDto> holidayResponseDtos;

}
