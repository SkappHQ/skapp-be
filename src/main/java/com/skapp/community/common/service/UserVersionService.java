package com.skapp.community.common.service;

import com.skapp.enterprise.common.type.VersionType;

public interface UserVersionService {

	void upgradeUserVersion(Long userId, VersionType versionType);

	String getUserVersion(Long userId);

}
