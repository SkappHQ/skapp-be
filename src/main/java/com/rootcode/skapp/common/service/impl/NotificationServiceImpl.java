package com.rootcode.skapp.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rootcode.skapp.common.component.WebSocketHandler;
import com.rootcode.skapp.common.constant.CommonMessageConstant;
import com.rootcode.skapp.common.exception.ModuleException;
import com.rootcode.skapp.common.model.Notification;
import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.common.payload.request.NotificationsFilterDto;
import com.rootcode.skapp.common.payload.response.NotificationResponseDto;
import com.rootcode.skapp.common.payload.response.PageDto;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.common.repository.NotificationDao;
import com.rootcode.skapp.common.service.NotificationService;
import com.rootcode.skapp.common.service.UserService;
import com.rootcode.skapp.common.type.EmailBodyTemplates;
import com.rootcode.skapp.common.type.NotificationCategory;
import com.rootcode.skapp.common.type.NotificationType;
import com.rootcode.skapp.common.util.MessageUtil;
import com.rootcode.skapp.common.util.transformer.PageTransformer;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.repository.EmployeeDao;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	@NonNull
	private final WebSocketHandler webSocketHandler;

	@NonNull
	private final NotificationDao notificationDao;

	@NonNull
	private final UserService userService;

	@NonNull
	private final PageTransformer pageTransformer;

	@NonNull
	private final MessageUtil messageUtil;

	private static final String NOTIFICATION_LANGUAGE = "en";

	private final EmployeeDao employeeDao;

	@Override
	public void createNotification(Employee employee, String resourceId, NotificationType notificationType,
			EmailBodyTemplates emailBodyTemplates, Object dynamicFields, NotificationCategory notificationCategory) {
		log.info("createNotification: execution started");

		try {
			Notification notification = new Notification();
			notification.setEmployee(employee);
			notification.setResourceId(resourceId);
			notification.setBody(getNotificationBody(emailBodyTemplates, dynamicFields, notificationCategory));
			notification.setIsViewed(false);
			notification.setNotificationType(notificationType);
			notificationDao.save(notification);

			webSocketHandler.sendNotificationToUser(notification.getEmployee().getEmployeeId().toString(),
					notification.getBody());
		}
		catch (Exception e) {
			log.error("createNotification: ", e);
			return;
		}

		log.info("createNotification: execution ended");
	}

	private String getNotificationBody(EmailBodyTemplates emailBodyTemplates, Object dynamicFields,
			NotificationCategory notificationCategory) {
		String templateMessage = getNotificationMessageFromNotificationTemplates(emailBodyTemplates,
				notificationCategory);
		Map<String, String> placeholders = convertObjectToMap(dynamicFields);
		Map<String, Map<String, Map<String, String>>> translations = loadEnumTranslations();
		Map<String, Map<String, String>> languageTranslations = translations.getOrDefault(NOTIFICATION_LANGUAGE,
				new HashMap<>());
		Map<String, String> translatedPlaceholders = placeholders.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> {
				String key = entry.getKey();
				String value = entry.getValue();

				return languageTranslations.containsKey(key) && languageTranslations.get(key).containsKey(value)
						? languageTranslations.get(key).get(value) : value;
			}));
		return replacePlaceholders(templateMessage, translatedPlaceholders);
	}

	private String replacePlaceholders(String body, Map<String, String> values) {
		if (body == null || values == null) {
			return body;
		}

		for (Map.Entry<String, String> entry : values.entrySet()) {
			String placeholder = "{{" + entry.getKey() + "}}";
			String value = entry.getValue() != null ? entry.getValue() : "";
			body = body.replace(placeholder, value);
		}

		return body;
	}

	private Map<String, String> convertObjectToMap(Object dynamicFields) {
		if (dynamicFields == null) {
			return Collections.emptyMap();
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> fieldValues = objectMapper.convertValue(dynamicFields, new TypeReference<>() {
			});

			return fieldValues.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						entry -> entry.getValue() != null ? entry.getValue().toString() : ""));
		}
		catch (Exception e) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_FAILED_TO_CONVERT_DYNAMIC_VALUES);
		}
	}

	private String getNotificationMessageFromNotificationTemplates(EmailBodyTemplates emailBodyTemplates,
			NotificationCategory notificationCategory) {

		File file;
		try {
			file = new ClassPathResource("templates/notification/notification-templates.json").getFile();
		}
		catch (IOException exception) {
			log.error("Unable to find notification-templates.json file", exception);
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_NOTIFICATION_TEMPLATE_LOADING_FAILED);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(file);

			JsonNode languageNode = rootNode.path(NOTIFICATION_LANGUAGE);

			if (languageNode.isMissingNode()) {
				throw new ModuleException(CommonMessageConstant.COMMON_ERROR_FAILED_TO_LOAD_NOTIFICATION_LANGUAGE,
						new String[] { NOTIFICATION_LANGUAGE });
			}

			JsonNode category = languageNode.path(notificationCategory.getLabel());

			if (category.isMissingNode() || !category.isArray()) {
				throw new ModuleException(CommonMessageConstant.COMMON_ERROR_TEMPLATE_NOT_FOUND_FOR_LANGUAGE,
						new String[] { NOTIFICATION_LANGUAGE });
			}

			for (JsonNode templateNode : category) {
				if (templateNode.path("id").asText().equals(emailBodyTemplates.getTemplateId())) {
					String message = templateNode.path("message").asText();
					log.info("getTemplateFromJson: execution ended");
					return message;
				}
			}

			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_TEMPLATE_ID_NOT_FOUND,
					new String[] { emailBodyTemplates.getTemplateId() });
		}
		catch (IOException e) {
			log.error("Error occurred while parsing the notification templates", e);
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_NOTIFICATION_TEMPLATE_PARSING_FAILED);
		}
	}

	private Map<String, Map<String, Map<String, String>>> loadEnumTranslations() {
		ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
		try (InputStream inputStream = new ClassPathResource("templates/common/enum-translations.yml")
			.getInputStream()) {
			return yamlMapper.readValue(inputStream, new TypeReference<>() {
			});
		}
		catch (IOException e) {
			log.error("Failed to load enum-translations.yml: {}", e.getMessage());
			return new HashMap<>();
		}
	}

	@Override
	public ResponseEntityDto getAllNotifications(NotificationsFilterDto notificationsFilterDto) {
		log.info("getAllNotifications: execution started");

		Pageable pageable = PageRequest.of(notificationsFilterDto.getPage(), notificationsFilterDto.getSize(),
				Sort.by(notificationsFilterDto.getSortOrder(), notificationsFilterDto.getSortKey().toString()));

		Long userId = userService.getCurrentUser().getUserId();
		Page<Notification> notificationPage = notificationDao.findAllByUserIDAndNotificationFilterDto(userId,
				notificationsFilterDto, pageable);

		List<NotificationResponseDto> notificationsList = mapNotifications(
				notificationPage.hasContent() ? notificationPage.getContent() : Collections.emptyList());

		PageDto pageDto = pageTransformer.transform(notificationPage);
		pageDto.setItems(notificationsList);

		log.info("getAllNotifications: execution ended");
		return new ResponseEntityDto(false, pageDto);
	}

	@Override
	public ResponseEntityDto markNotificationAsRead(Long id) {
		log.info("markNotificationAsRead: execution started");

		User currentUser = userService.getCurrentUser();

		Optional<Notification> optionalNotification = notificationDao.findById(id);
		if (optionalNotification.isEmpty()
				|| !Objects.equals(optionalNotification.get().getEmployee().getEmployeeId(), currentUser.getUserId())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_NOTIFICATION_NOT_FOUND);
		}
		Notification notification = optionalNotification.get();

		notification.setIsViewed(true);
		notificationDao.save(notification);

		log.info("markNotificationAsRead: execution ended");
		return new ResponseEntityDto(false, "");
	}

	@Transactional
	@Override
	public ResponseEntityDto markAllNotificationsAsRead() {
		log.info("markAllNotificationsAsRead: execution started");

		User currentUser = userService.getCurrentUser();

		List<Notification> notifications = notificationDao.findByEmployee(currentUser.getEmployee());
		notifications.forEach(notification -> notification.setIsViewed(true));

		notificationDao.saveAll(notifications);

		log.info("markAllNotificationsAsRead: execution ended");
		return new ResponseEntityDto(false, "");
	}

	@Override
	public ResponseEntityDto getUnreadNotificationsCount() {
		log.info("getUnreviewedNotificationsCount: execution started");

		Long userId = userService.getCurrentUser().getUserId();
		long unreadCount = notificationDao.countUnreadNotificationsByUserId(userId);

		log.info("getUnreviewedNotificationsCount: execution ended");
		return new ResponseEntityDto(false, unreadCount);
	}

	public List<NotificationResponseDto> mapNotifications(List<Notification> notifications) {
		return notifications.stream().map(notification -> {
			NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
			notificationResponseDto.setId(notification.getId());
			notificationResponseDto.setCreatedDate(notification.getCreatedDate());
			notificationResponseDto.setBody(notification.getBody());
			notificationResponseDto.setIsViewed(notification.getIsViewed());
			notificationResponseDto.setResourceId(notification.getResourceId());
			notificationResponseDto.setNotificationType(notification.getNotificationType());
			notificationResponseDto.setIsCausedByCurrentUser(
					notification.getCreatedBy().equals(userService.getCurrentUser().getUserId().toString()));

			String createdBy = notification.getCreatedBy();
			if (createdBy != null) {
				Employee employee = employeeDao.getEmployeeByEmployeeId(Long.parseLong(createdBy));
				if (employee != null) {
					notificationResponseDto.setAuthPic(employee.getAuthPic());
				}
			}

			return notificationResponseDto;
		}).toList();
	}

}
