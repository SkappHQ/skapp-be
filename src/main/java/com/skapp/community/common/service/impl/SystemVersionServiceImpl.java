package com.skapp.community.common.service.impl;

import com.skapp.community.common.model.SystemVersion;
import com.skapp.community.common.repository.SystemVersionDao;
import com.skapp.community.common.service.SystemVersionService;
import com.skapp.community.common.type.SystemVersionTypes;
import com.skapp.enterprise.common.type.VersionType;
import com.skapp.enterprise.common.util.VersionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemVersionServiceImpl implements SystemVersionService {

	private final SystemVersionDao systemVersionDao;

	@Override
	public void upgradeSystemVersion(VersionType versionType, SystemVersionTypes systemVersionType) {
		SystemVersion latestVersion = systemVersionDao.findFirstByOrderByVersionDesc();

		String currentVersion = (latestVersion != null) ? latestVersion.getVersion() : "1.0.0";

		String newVersion = VersionUtil.incrementVersion(currentVersion, versionType);

		SystemVersion newSystemVersion = new SystemVersion();
		newSystemVersion.setVersion(newVersion);
		newSystemVersion.setReason(systemVersionType);
		systemVersionDao.save(newSystemVersion);
	}

}
