package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.leaveplanner.model.LeaveRequest;
import com.rootcode.skapp.peopleplanner.model.Holiday;

public interface PeopleNotificationService {

	void sendNewHolidayDeclarationNotification(Holiday holiday);

	void sendHolidaySingleDayPendingLeaveRequestCancellationEmployeeNotification(LeaveRequest leaveRequest,
			Holiday holiday);

	void sendHolidayMultipleDayPendingLeaveRequestUpdatedEmployeeNotification(LeaveRequest leaveRequest,
			Holiday holiday);

	void sendHolidaySingleDayApprovedLeaveRequestRevokedEmployeeNotification(LeaveRequest leaveRequest,
			Holiday holiday);

	void sendHolidayMultipleDayApprovedLeaveRequestUpdatedEmployeeNotification(LeaveRequest leaveRequest,
			Holiday holiday);

	void sendHolidaySingleDayPendingLeaveRequestCancellationManagerNotification(LeaveRequest leaveRequest,
			Holiday holiday);

	void sendHolidayMultipleDayPendingLeaveRequestUpdatedManagerNotification(LeaveRequest leaveRequest,
			Holiday holiday);

	void sendHolidaySingleDayApprovedLeaveRequestRevokedManagerNotification(LeaveRequest leaveRequest, Holiday holiday);

	void sendHolidayMultipleDayApprovedLeaveRequestUpdatedManagerEmail(LeaveRequest leaveRequest, Holiday holiday);

	void sendPasswordResetRequestManagerNotification(User user, String requestDateTime);

}
