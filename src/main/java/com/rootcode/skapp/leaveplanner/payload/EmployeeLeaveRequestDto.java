package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.model.LeaveRequest;
import com.rootcode.skapp.peopleplanner.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeLeaveRequestDto {

	private Employee employee;

	private LeaveRequest leaveRequest;

}
