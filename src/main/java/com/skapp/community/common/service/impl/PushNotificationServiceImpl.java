package com.skapp.community.common.service.impl;

import com.skapp.community.common.component.WebSocketHandler;
import com.skapp.community.common.model.Notification;
import com.skapp.community.common.service.PushNotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

	@NonNull
	private final WebSocketHandler webSocketHandler;

	@Override
	public void sendNotification(Long userId, Notification notification) {
		webSocketHandler.sendNotificationToUser(userId.toString(), notification.getBody());
	}

}
