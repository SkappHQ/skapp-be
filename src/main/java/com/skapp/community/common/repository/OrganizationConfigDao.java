package com.skapp.community.common.repository;

import com.skapp.community.common.model.OrganizationConfig;
import com.skapp.community.common.type.OrganizationConfigType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrganizationConfigDao
		extends JpaRepository<OrganizationConfig, Long>, JpaSpecificationExecutor<OrganizationConfig> {

	Optional<OrganizationConfig> findOrganizationConfigByOrganizationConfigType(
			OrganizationConfigType organizationConfigType);

}
