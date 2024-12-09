package com.skapp.enterprise.common.constant;

import com.skapp.community.common.constant.MessageConstant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPCommonMessageConstant implements MessageConstant {

	EP_COMMON_ERROR_INVALID_LOGIN_METHOD("ep.common.error.invalid-login-method"),
	EP_COMMON_ERROR_TENANT_NOT_PRESENT("ep.common.error.tenant-not-present"),
	EP_COMMON_ERROR_TENANT_CONTEXT_ERROR("ep.common.error.tenant-context-error"),
	EP_COMMON_ERROR_TENANT_NOT_FOUND("ep.common.error.tenant-not-found"),
	EP_COMMON_ERROR_TENANT_ALREADY_EXISTS("ep.common.error.tenant-already-exists"),
	EP_COMMON_ERROR_TENANT_CREATION_ERROR("ep.common.error.tenant-creation-error"),
	EP_COMMON_ERROR_TENANT_CREATION_UNEXPECTED_ERROR("ep.common.error.tenant-creation-unexpected-error"),;

	private final String messageKey;

}
