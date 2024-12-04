package com.rootcode.skapp.leaveplanner.payload.request;

import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllLeaveRequestsResponseDto extends LeaveRequestResponseDto {

	private EmployeeBasicDetailsResponseDto employee;

}
