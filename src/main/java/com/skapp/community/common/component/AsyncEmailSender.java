package com.skapp.community.common.component;

import com.skapp.community.common.type.EmailBodyTemplates;

import java.util.Map;

public interface AsyncEmailSender {

	void sendMail(String to, String subject, String htmlBody, EmailBodyTemplates emailTemplate,
			Map<String, String> placeholders);

}
