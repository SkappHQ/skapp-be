package com.skapp.community.common.service;

import com.skapp.community.common.payload.request.TestEmailServerRequestDto;
import com.skapp.community.common.type.EmailBodyTemplates;

public interface EmailService {

	void testEmailServer(TestEmailServerRequestDto testEmailServerRequestDto);

	void sendEmail(EmailBodyTemplates emailTemplate, Object dynamicFeildsObject, String recipient);

}
