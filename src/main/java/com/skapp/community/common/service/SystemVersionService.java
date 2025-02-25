package com.skapp.community.common.service;

import com.skapp.community.common.type.SystemVersionTypes;
import com.skapp.enterprise.common.type.VersionType;

public interface SystemVersionService {

	void upgradeSystemVersion(VersionType versionType, SystemVersionTypes systemVersionType);

	String getLatestVersion();

}
