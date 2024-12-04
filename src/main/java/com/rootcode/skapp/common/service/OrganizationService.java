package com.rootcode.skapp.common.service;

import com.rootcode.skapp.common.payload.request.EmailServerRequestDto;
import com.rootcode.skapp.common.payload.request.OrganizationDto;
import com.rootcode.skapp.common.payload.request.UpdateOrganizationRequestDto;
import com.rootcode.skapp.common.payload.response.EmailServerConfigResponseDto;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;

public interface OrganizationService {

	ResponseEntityDto saveOrganization(OrganizationDto organizationDto);

	ResponseEntityDto getOrganization();

	ResponseEntityDto saveEmailServerConfigs(EmailServerRequestDto emailServerRequestDto);

	EmailServerConfigResponseDto getEmailServiceConfigs();

	ResponseEntityDto getOrganizationConfigs();

	ResponseEntityDto updateOrganization(UpdateOrganizationRequestDto organizationDto);

}
