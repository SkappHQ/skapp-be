package com.skapp.community.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public enum CacheKeys {

	SYSTEM_VERSION_CACHE_KEY("system_version_cache:latest_version", 7, TimeUnit.DAYS),
	USER_VERSION_CACHE_KEY("user_version_cache:%s:latest_version", 7, TimeUnit.DAYS), // userId
	UUID_CACHE_KEY("uuid_cache:%s", 5, TimeUnit.MINUTES);

	private final String key;

	private final long ttl;

	private final TimeUnit timeUnit;

	public String format(Object... values) {
		return key.formatted(values);
	}

}
