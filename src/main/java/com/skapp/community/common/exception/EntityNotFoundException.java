package com.skapp.community.common.exception;

import com.skapp.community.common.constant.MessageConstant;
import com.skapp.community.common.util.MessageUtil;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Getter
public class EntityNotFoundException extends RuntimeException {

	private final MessageConstant messageKey;

	private static MessageUtil messageUtil;

	@Component
	public static class MessageUtilInjector implements ApplicationContextAware {

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) {
			messageUtil = applicationContext.getBean(MessageUtil.class);
		}

	}

	public EntityNotFoundException(MessageConstant messageKey) {
		super(messageUtil.getMessage(messageKey.getMessageKey()));
		this.messageKey = messageKey;
	}

	public EntityNotFoundException(MessageConstant messageKey, Object[] args) {
		super(messageUtil.getMessage(messageKey.getMessageKey(), args));
		this.messageKey = messageKey;
	}

}
