package com.rootcode.skapp.common.repository;

import com.rootcode.skapp.common.model.Notification;
import com.rootcode.skapp.common.type.NotificationType;
import com.rootcode.skapp.peopleplanner.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDao extends JpaRepository<Notification, Long>, NotificationRepository {

	List<Notification> findByEmployee(Employee employee);

	Notification findFirstByResourceIdAndNotificationTypeOrderByCreatedDateDesc(String leaveRequestId,
			NotificationType notificationType);

}
