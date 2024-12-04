package com.rootcode.skapp.leaveplanner.repository;

import com.rootcode.skapp.leaveplanner.model.LeaveType;

import java.util.List;

public interface LeaveTypeRepository {

	List<LeaveType> getLeaveTypesByCarryForwardEnable(boolean carryForward, List<Long> leaveTypeIds);

	List<LeaveType> getUsedUserLeaveTypes(Long userId, boolean isCarryForward);

}
