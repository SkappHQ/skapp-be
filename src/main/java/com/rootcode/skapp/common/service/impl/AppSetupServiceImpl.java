package com.rootcode.skapp.common.service.impl;

import com.rootcode.skapp.common.payload.response.OrganizationStatusResponseDto;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.common.repository.OrganizationConfigDao;
import com.rootcode.skapp.common.service.AppSetupStatusService;
import com.rootcode.skapp.peopleplanner.repository.EmployeeRoleDao;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppSetupServiceImpl implements AppSetupStatusService {

	@NonNull
	private final OrganizationConfigDao organizationConfigDao;

	@NonNull
	private final EmployeeRoleDao employeeRoleDao;

	@Override
	public ResponseEntityDto getAppSetupStatus() {
		log.info("getAppSetupStatus: execution started");

		OrganizationStatusResponseDto organizationStatusResponseDto = new OrganizationStatusResponseDto();
		organizationStatusResponseDto.setIsOrganizationSetupCompleted(organizationConfigDao.count() > 0);
		organizationStatusResponseDto.setIsSignUpCompleted(employeeRoleDao.existsByIsSuperAdminTrue());

		log.info("getAppSetupStatus: execution ended");
		return new ResponseEntityDto(false, organizationStatusResponseDto);
	}

}
