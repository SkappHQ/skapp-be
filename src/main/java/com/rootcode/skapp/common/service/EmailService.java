package com.rootcode.skapp.common.service;

import com.rootcode.skapp.common.payload.request.TestEmailServerRequestDto;
import com.rootcode.skapp.common.type.EmailBodyTemplates;

public interface EmailService {

	void testEmailServer(TestEmailServerRequestDto testEmailServerRequestDto);

	void sendEmail(EmailBodyTemplates emailTemplate, Object dynamicFeildsObject, String recipient);

}
