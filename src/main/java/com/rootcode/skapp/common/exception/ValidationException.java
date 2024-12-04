package com.rootcode.skapp.common.exception;

import com.rootcode.skapp.common.constant.MessageConstant;
import com.rootcode.skapp.common.util.MessageUtil;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

	private final MessageConstant messageKey;

	private final List<String> validationErrors;

	private static MessageUtil messageUtil;

	@Component
	public static class MessageUtilInjector implements ApplicationContextAware {

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) {
			messageUtil = applicationContext.getBean(MessageUtil.class);
		}

	}

	public ValidationException(MessageConstant messageKey) {
		super(messageUtil.getMessage(messageKey.getMessageKey()));
		this.messageKey = messageKey;
		this.validationErrors = null;
	}

	public ValidationException(MessageConstant messageKey, List<String> validationErrors) {
		super(messageUtil.getMessage(messageKey.getMessageKey(), new Object[] { String.join(", ", validationErrors) }));
		this.messageKey = messageKey;
		this.validationErrors = validationErrors;
	}

}
