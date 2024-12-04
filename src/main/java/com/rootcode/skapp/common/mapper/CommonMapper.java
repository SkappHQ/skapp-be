package com.rootcode.skapp.common.mapper;

import com.rootcode.skapp.common.model.Notification;
import com.rootcode.skapp.common.model.Organization;
import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.common.payload.request.OrganizationDto;
import com.rootcode.skapp.common.payload.request.SuperAdminSignUpRequestDto;
import com.rootcode.skapp.common.payload.response.NotificationResponseDto;
import com.rootcode.skapp.common.payload.response.OrganizationResponseDto;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.Holiday;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeTeamResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommonMapper {

	Organization organizationDtoToOrganization(OrganizationDto organizationDto);

	User createSuperAdminRequestDtoToUser(SuperAdminSignUpRequestDto superAdminSignUpRequestDto);

	EmployeeTeamResponseDto employeeToEmployeeTeamResponseDto(Employee employee);

	OrganizationResponseDto organizationToOrganizationResponseDto(Organization organization);

	HolidayResponseDto holidayToHolidayResponseDto(Holiday holiday);

	List<NotificationResponseDto> notificationListToNotificationResponseDtoList(List<Notification> notifications);

}
