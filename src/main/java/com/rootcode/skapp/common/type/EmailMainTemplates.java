package com.rootcode.skapp.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailMainTemplates {

	MAIN_TEMPLATE_V1("main-template-v1");

	private final String templateId;

}
