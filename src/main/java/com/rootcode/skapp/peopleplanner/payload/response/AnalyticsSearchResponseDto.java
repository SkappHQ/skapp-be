package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.leaveplanner.payload.EmployeeSummarizedResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AnalyticsSearchResponseDto {

	List<EmployeeSummarizedResponseDto> employeeResponseDtoList;

	List<TeamDetailResponseDto> teamResponseDtoList;

}
