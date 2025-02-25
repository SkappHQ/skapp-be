package com.skapp.community.common.service.impl;

import com.skapp.community.common.constant.AuthConstants;
import com.skapp.community.common.model.UserVersion;
import com.skapp.community.common.repository.UserVersionDao;
import com.skapp.community.common.service.UserVersionService;
import com.skapp.enterprise.common.type.VersionType;
import com.skapp.enterprise.common.util.VersionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVersionServiceImpl implements UserVersionService {

	private final UserVersionDao userVersionDao;

	@Override
	public void upgradeUserVersion(Long userId, VersionType versionType) {
		UserVersion userVersion = userVersionDao.findByUserId(userId);

		String currentVersion = (userVersion != null) ? userVersion.getVersion() : AuthConstants.DEFAULT_USER_VERSION;
		String newVersion = VersionUtil.incrementVersion(currentVersion, versionType);

		if (userVersion == null) {
			userVersion = new UserVersion();
			userVersion.setUserId(userId);
		}
		userVersion.setVersion(newVersion);
		userVersionDao.save(userVersion);
	}

}
