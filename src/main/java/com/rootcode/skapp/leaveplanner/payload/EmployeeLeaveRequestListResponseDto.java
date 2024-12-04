package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeaveRequestListResponseDto {

	private EmployeeResponseDto employeeResponseDto;

	private List<LeaveRequestResponseDto> leaveRequestResponseDto;

}
