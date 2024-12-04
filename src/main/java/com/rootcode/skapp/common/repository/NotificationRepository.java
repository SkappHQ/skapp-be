package com.rootcode.skapp.common.repository;

import com.rootcode.skapp.common.model.Notification;
import com.rootcode.skapp.common.payload.request.NotificationsFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository {

	Page<Notification> findAllByUserIDAndNotificationFilterDto(Long userId,
			NotificationsFilterDto notificationsFilterDto, Pageable pageable);

	long countUnreadNotificationsByUserId(Long userId);

}
