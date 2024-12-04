package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Data;

@Data
public class EmployeeTeamResponseDto {

	private EmployeeBasicDetailsResponseDto employee;

	private Boolean isSupervisor;

}
