package com.rootcode.skapp.common.repository;

import com.rootcode.skapp.common.model.OrganizationConfig;
import com.rootcode.skapp.common.type.OrganizationConfigType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrganizationConfigDao
		extends JpaRepository<OrganizationConfig, Long>, JpaSpecificationExecutor<OrganizationConfig> {

	Optional<OrganizationConfig> findOrganizationConfigByOrganizationConfigType(
			OrganizationConfigType organizationConfigType);

}
