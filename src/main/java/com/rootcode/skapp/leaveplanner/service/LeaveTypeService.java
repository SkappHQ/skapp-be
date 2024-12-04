package com.rootcode.skapp.leaveplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveTypeFilterDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveTypePatchRequestDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveTypeRequestDto;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface LeaveTypeService {

	@Transactional(propagation = Propagation.REQUIRED)
	ResponseEntityDto addLeaveType(LeaveTypeRequestDto leaveTypeRequestDto);

	ResponseEntityDto getLeaveTypes(LeaveTypeFilterDto leaveTypeFilterDto);

	ResponseEntityDto getLeaveTypeById(Long id);

	ResponseEntityDto updateLeaveType(Long id, LeaveTypePatchRequestDto leaveTypePatchRequestDto);

	void createDefaultLeaveType();

}
