package com.rootcode.skapp.common.service;

import com.rootcode.skapp.common.payload.request.NotificationsFilterDto;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.common.type.EmailBodyTemplates;
import com.rootcode.skapp.common.type.NotificationCategory;
import com.rootcode.skapp.common.type.NotificationType;
import com.rootcode.skapp.peopleplanner.model.Employee;

public interface NotificationService {

	void createNotification(Employee employee, String resourceId, NotificationType notificationType,
			EmailBodyTemplates emailBodyTemplates, Object commonEmailDynamicFields,
			NotificationCategory notificationCategory);

	ResponseEntityDto getAllNotifications(NotificationsFilterDto notificationsFilterDto);

	ResponseEntityDto markNotificationAsRead(Long id);

	ResponseEntityDto markAllNotificationsAsRead();

	ResponseEntityDto getUnreadNotificationsCount();

}
