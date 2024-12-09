package com.skapp.community.peopleplanner.service;

import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeBulkDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDataValidationDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeFilterDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeQuickAddDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.NotificationSettingsPatchRequestDto;
import com.skapp.community.peopleplanner.payload.request.PermissionFilterDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeManagerResponseDto;

import java.util.List;

public interface PeopleService {

	ResponseEntityDto addNewEmployee(EmployeeDetailsDto employeeDetailsDto);

	ResponseEntityDto quickAddEmployee(EmployeeQuickAddDto employeeQuickAddDto);

	ResponseEntityDto updateEmployee(Long employeeId, EmployeeUpdateDto employeeUpdateDto);

	ResponseEntityDto getEmployees(EmployeeFilterDto employeeFilterDto);

	ResponseEntityDto getEmployeeById(Long employeeId);

	ResponseEntityDto getCurrentEmployee();

	ResponseEntityDto addBulkEmployees(List<EmployeeBulkDto> employeeBulkDto);

	ResponseEntityDto getLoginPendingEmployeeCount();

	ResponseEntityDto searchEmployeesByNameOrEmail(PermissionFilterDto permissionFilterDto);

	ResponseEntityDto searchEmployeesByEmail(String email);

	ResponseEntityDto getEmployeeByIdOrEmail(EmployeeDataValidationDto employeeDataValidationDto);

	ResponseEntityDto updateLoggedInUser(Long employeeId, EmployeeUpdateDto employeeUpdateDto);

	ResponseEntityDto terminateUser(Long userId);

	List<EmployeeManagerResponseDto> getCurrentEmployeeManagers();

	ResponseEntityDto updateNotificationSettings(
			NotificationSettingsPatchRequestDto notificationSettingsPatchRequestDto);

	ResponseEntityDto getNotificationSettings();

	boolean isManagerAvailableForCurrentEmployee();

	ResponseEntityDto searchEmployeesAndTeamsByKeyword(String keyword);

}
