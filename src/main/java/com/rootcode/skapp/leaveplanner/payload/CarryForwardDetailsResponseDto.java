package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.peopleplanner.payload.response.EmployeeResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CarryForwardDetailsResponseDto {

	private EmployeeResponseDto employee;

	private List<CarryForwardEntitlementDto> entitlements;

}
