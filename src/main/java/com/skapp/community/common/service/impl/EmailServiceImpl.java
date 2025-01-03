package com.skapp.community.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.skapp.community.common.component.AsyncEmailSender;
import com.skapp.community.common.model.Organization;
import com.skapp.community.common.payload.email.EmailTemplateMetadata;
import com.skapp.community.common.payload.request.TestEmailServerRequestDto;
import com.skapp.community.common.repository.OrganizationDao;
import com.skapp.community.common.service.EmailService;
import com.skapp.community.common.type.EmailBodyTemplates;
import com.skapp.community.common.type.EmailMainTemplates;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	private static final String EMAIL_LANGUAGE = "en";

	@NonNull
	private final AsyncEmailSender asyncEmailSender;

	private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

	private Map<String, Map<String, List<EmailTemplateMetadata>>> templateDetailsMap;

	private Map<String, Map<String, Map<String, String>>> enumTranslationsMap;

	@NonNull
	private OrganizationDao organizationDao;

	@Override
	public void testEmailServer(TestEmailServerRequestDto testEmailServerRequestDto) {
		asyncEmailSender.sendMail(testEmailServerRequestDto.getEmail(), testEmailServerRequestDto.getSubject(),
				testEmailServerRequestDto.getBody(), null, null);
	}

	@Override
	public void sendEmail(EmailBodyTemplates emailTemplate, Object dynamicFieldsObject, String recipient) {
		try {
			if (emailTemplate == null || recipient == null) {
				log.error("Email template or recipient is null");
				return;
			}

			EmailTemplateMetadata templateDetails = getTemplateDetails(emailTemplate.getTemplateId());
			if (templateDetails == null) {
				log.error("Template not found for ID: {}", emailTemplate.getTemplateId());
				return;
			}

			String module = findModuleForTemplate(emailTemplate.getTemplateId());
			if (module == null) {
				log.error("Module not found for template ID: {}", emailTemplate.getTemplateId());
				return;
			}

			Map<String, String> placeholders = convertDtoToMap(dynamicFieldsObject);
			placeholders.replaceAll(this::getLocalizedEnumValue);
			placeholders.put("subject", templateDetails.getSubject());

			if (emailTemplate != EmailBodyTemplates.COMMON_MODULE_EMAIL_VERIFY
					&& emailTemplate != EmailBodyTemplates.COMMON_MODULE_SSO_CREATION_TENANT_URL
					&& emailTemplate != EmailBodyTemplates.COMMON_MODULE_CREDENTIAL_BASED_CREATION_TENANT_URL) {
				Optional<Organization> organization = organizationDao.findTopByOrderByOrganizationIdDesc();
				organization.ifPresent(value -> placeholders.put("appUrl", value.getAppUrl()));
			}

			String emailBody = buildEmailBody(templateDetails, module, placeholders);
			asyncEmailSender.sendMail(recipient, templateDetails.getSubject(), emailBody, emailTemplate, placeholders);
		}
		catch (Exception e) {
			log.error("Unexpected error in email sending process: {}", e.getMessage(), e);
		}
	}

	private void loadTemplateDetails() {
		if (templateDetailsMap == null) {
			try (InputStream inputStream = new ClassPathResource("community/templates/email/email-templates.yml")
				.getInputStream()) {
				templateDetailsMap = yamlMapper.readValue(inputStream, new TypeReference<>() {
				});
			}
			catch (IOException e) {
				log.error("Failed to load email-templates.yml: {}", e.getMessage());
				templateDetailsMap = new HashMap<>();
			}
		}
	}

	private void loadEnumTranslations() {
		if (enumTranslationsMap == null) {
			try (InputStream inputStream = new ClassPathResource("community/templates/common/enum-translations.yml")
				.getInputStream()) {
				enumTranslationsMap = yamlMapper.readValue(inputStream, new TypeReference<>() {
				});
			}
			catch (IOException e) {
				log.error("Failed to load enum-translations.yml: {}", e.getMessage());
				enumTranslationsMap = new HashMap<>();
			}
		}
	}

	private String getLocalizedEnumValue(String enumKey, String enumValue) {
		loadEnumTranslations();
		return Optional.ofNullable(enumTranslationsMap.get(EmailServiceImpl.EMAIL_LANGUAGE))
			.map(langMap -> langMap.get(enumKey))
			.map(enumMap -> enumMap.get(enumValue))
			.orElse(enumValue);
	}

	private EmailTemplateMetadata getTemplateDetails(String templateId) {
		loadTemplateDetails();
		return templateDetailsMap.getOrDefault(EMAIL_LANGUAGE, Collections.emptyMap())
			.values()
			.stream()
			.flatMap(Collection::stream)
			.filter(template -> template.getId().equals(templateId))
			.findFirst()
			.orElse(null);
	}

	private String findModuleForTemplate(String templateId) {
		loadTemplateDetails();
		return templateDetailsMap.getOrDefault(EMAIL_LANGUAGE, Collections.emptyMap())
			.entrySet()
			.stream()
			.filter(entry -> entry.getValue().stream().anyMatch(template -> template.getId().equals(templateId)))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}

	private String buildEmailBody(EmailTemplateMetadata templateDetails, String module,
			Map<String, String> placeholders) throws IOException {
		String templatePath = String.format("community/templates/email/%s/%s/%s.html", EMAIL_LANGUAGE, module,
				templateDetails.getId());
		String body = replaceValuesToTemplate(templatePath, placeholders);
		String mainTemplatePath = String.format("community/templates/email/%s/%s.html", EMAIL_LANGUAGE,
				EmailMainTemplates.MAIN_TEMPLATE_V1.getTemplateId());
		placeholders.put("body", body);
		return replaceValuesToTemplate(mainTemplatePath, placeholders);
	}

	private String replaceValuesToTemplate(String templatePath, Map<String, String> placeholders) throws IOException {
		ClassPathResource resource = new ClassPathResource(templatePath);
		String templateContent = new String(resource.getInputStream().readAllBytes());
		for (Map.Entry<String, String> entry : placeholders.entrySet()) {
			String replacement = entry.getValue() == null ? "" : entry.getValue();
			templateContent = templateContent.replace("{{" + entry.getKey() + "}}", replacement);
		}
		return templateContent;
	}

	private Map<String, String> convertDtoToMap(Object data) {
		Map<String, String> placeholders = new HashMap<>();
		BeanMap beanMap = BeanMap.create(data);
		for (Object entry : beanMap.entrySet()) {
			Map.Entry<?, ?> mapEntry = (Map.Entry<?, ?>) entry;
			placeholders.put(mapEntry.getKey().toString(), String.valueOf(mapEntry.getValue()));
		}
		return placeholders;
	}

}
