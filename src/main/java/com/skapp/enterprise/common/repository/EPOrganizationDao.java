package com.skapp.enterprise.common.repository;

import com.skapp.enterprise.common.model.EPOrganizationConfig;
import com.skapp.enterprise.common.type.EPOrganizationConfigType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EPOrganizationDao
		extends JpaRepository<EPOrganizationConfig, Long>, JpaSpecificationExecutor<EPOrganizationConfig> {

	Optional<EPOrganizationConfig> findEPOrganizationConfigByEpOrganizationConfigType(
			EPOrganizationConfigType organizationConfigType);

}
