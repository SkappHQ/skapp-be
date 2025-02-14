package com.skapp.community.common.mapper;

import com.skapp.community.common.model.Organization;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.request.OrganizationDto;
import com.skapp.community.common.payload.request.SuperAdminSignUpRequestDto;
import com.skapp.community.common.payload.response.OrganizationResponseDto;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.Holiday;
import com.skapp.community.peopleplanner.payload.response.EmployeeTeamResponseDto;
import com.skapp.community.peopleplanner.payload.response.HolidayResponseDto;
import com.skapp.enterprise.common.model.master.Tenant;
import com.skapp.enterprise.common.payload.request.CreateSubscriptionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommonMapper {

	Organization organizationDtoToOrganization(OrganizationDto organizationDto);

	User createSuperAdminRequestDtoToUser(SuperAdminSignUpRequestDto superAdminSignUpRequestDto);

	EmployeeTeamResponseDto employeeToEmployeeTeamResponseDto(Employee employee);

	OrganizationResponseDto organizationToOrganizationResponseDto(Organization organization);

	HolidayResponseDto holidayToHolidayResponseDto(Holiday holiday);

	@Mapping(target = "subscriptionId", source = "stripeSubscription.subscriptionId")
	@Mapping(target = "customerId", source = "stripeSubscription.customerId")
	CreateSubscriptionResponseDto tenantToCreateSubscriptionResponseDto(Tenant tenant);

}
