package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.timeplanner.payload.projection.EmployeeWorkHours;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IndividualWorkHoursResponseDto {

	private int month;

	private String monthName;

	List<EmployeeWorkHours> employeeWorkHours;

}
