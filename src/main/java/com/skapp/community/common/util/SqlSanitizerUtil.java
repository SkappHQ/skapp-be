package com.skapp.community.common.util;

import java.util.regex.Pattern;

public class SqlSanitizerUtil {

	private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
			"(?i)(?:--|;|/\\*|\\*/|@@|\\b(?:char\\(|nchar\\(|varchar\\(|nvarchar\\(|alter|begin|cast\\(|create|cursor|declare|delete|drop|end|exec|execute|fetch|insert|kill|open|select|sys|sysobjects|syscolumns|table|update)\\b)");

	public static String sanitize(String input) {
		if (input == null || input.isBlank()) {
			return input;
		}

		String sanitized = SQL_INJECTION_PATTERN.matcher(input).replaceAll("");

		sanitized = sanitized.replace("'", "''");

		return sanitized.trim();
	}

}
