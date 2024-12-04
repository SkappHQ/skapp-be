package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeLeaveRequestResponseDto {

	private EmployeeBasicDetailsResponseDto employeeResponseDto;

	private LeaveRequestResponseDto leaveRequestResponseDto;

}
