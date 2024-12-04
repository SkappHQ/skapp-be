package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.leaveplanner.model.LeaveRequest;
import com.rootcode.skapp.peopleplanner.model.Holiday;

public interface PeopleEmailService {

	void sendUserInvitationEmail(User user);

	void sendUserTerminationEmail(User user);

	void sendNewHolidayDeclarationEmail(Holiday holiday);

	void sendHolidaySingleDayPendingLeaveRequestCancellationEmployeeEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidayMultipleDayPendingLeaveRequestUpdatedEmployeeEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidaySingleDayApprovedLeaveRequestRevokedEmployeeEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidayMultipleDayApprovedLeaveRequestUpdatedEmployeeEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidayMultipleDayApprovedLeaveRequestUpdatedManagerEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidayMultipleDayPendingLeaveRequestUpdatedManagerEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidaySingleDayPendingLeaveRequestCancellationManagerEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidaySingleDayApprovedLeaveRequestRevokedManagerEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendPasswordResetRequestManagerEmail(User user, String requestDateTime);

}
