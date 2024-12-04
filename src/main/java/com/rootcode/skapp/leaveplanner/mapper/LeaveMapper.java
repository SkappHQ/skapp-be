package com.rootcode.skapp.leaveplanner.mapper;

import com.rootcode.skapp.leaveplanner.model.CarryForwardInfo;
import com.rootcode.skapp.leaveplanner.model.LeaveEntitlement;
import com.rootcode.skapp.leaveplanner.model.LeaveRequest;
import com.rootcode.skapp.leaveplanner.model.LeaveType;
import com.rootcode.skapp.leaveplanner.payload.CarryForwardDetailsResponseDto;
import com.rootcode.skapp.leaveplanner.payload.CarryForwardEntitlementDto;
import com.rootcode.skapp.leaveplanner.payload.CustomEntitlementDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeEntitlementTeamJobRoleDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeLeaveReportResponseDto;
import com.rootcode.skapp.leaveplanner.payload.EntitlementDetailsDto;
import com.rootcode.skapp.leaveplanner.payload.EntitlementDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementReportDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementResponseDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveReportDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveTypeBasicInfoDto;
import com.rootcode.skapp.leaveplanner.payload.SummarizedCustomLeaveEntitlementDto;
import com.rootcode.skapp.leaveplanner.payload.request.AllLeaveRequestsResponseDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveRequestByIdResponseDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveRequestDto;
import com.rootcode.skapp.leaveplanner.payload.request.LeaveTypeRequestDto;
import com.rootcode.skapp.leaveplanner.payload.response.EmployeeLeaveEntitlementReportExportDto;
import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestManagerResponseDto;
import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestWithEmployeeResponseDto;
import com.rootcode.skapp.leaveplanner.payload.response.LeaveTypeBasicDetailsResponseDto;
import com.rootcode.skapp.leaveplanner.payload.response.LeaveTypeResponseDto;
import com.rootcode.skapp.leaveplanner.payload.response.SummarizedLeaveEntitlementBalanceDto;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.Holiday;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import com.rootcode.skapp.timeplanner.payload.response.ClockInSummaryLeaveRequestResponseDto;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LeaveMapper {

	@IterableMapping(elementTargetType = LeaveRequestResponseDto.class)
	List<LeaveRequestResponseDto> leaveRequestListToLeaveRequestResponseDtoList(List<LeaveRequest> leaveRequests);

	LeaveEntitlementResponseDto leaveEntitlementToEntitlementResponseDto(LeaveEntitlement leaveEntitlement);

	List<LeaveEntitlementResponseDto> leaveEntitlementsToEntitlementResponseDtoList(
			List<LeaveEntitlement> leaveEntitlements);

	CarryForwardInfo employeeLeaveTypeToCarryForwardInfo(Employee employeeToForward, LeaveType leaveType,
			LocalDate leaveCycleEndDate);

	@Mapping(target = "employee", source = "employeeToForward")
	@Mapping(target = "totalDaysUsed", constant = "0F")
	@Mapping(target = "reason", constant = "Carry Forward")
	@Mapping(target = "active", constant = "true")
	@Mapping(target = "manual", constant = "true")
	@Mapping(target = "override", ignore = true)
	@Mapping(target = "validFrom", ignore = true)
	@Mapping(target = "validTo", ignore = true)
	@Mapping(target = "leaveType", ignore = true)
	LeaveEntitlement employeeToLeaveEntitlement(Employee employeeToForward);

	@Mapping(target = "leaveTypeId", source = "leaveType.typeId")
	@Mapping(target = "name", source = "leaveType.name")
	@Mapping(target = "totalDaysAllocated", source = "leaveEntitlement.totalDaysAllocated")
	@Mapping(target = "totalDaysUsed", source = "leaveEntitlement.totalDaysUsed")
	CarryForwardEntitlementDto leaveTypeAndEntitlementToCarryForwardEntitlementDto(LeaveType leaveType,
			LeaveEntitlement leaveEntitlement);

	@Mapping(target = "employee.employeeId", source = "leaveEntitlement.employee.employeeId")
	@Mapping(target = "employee.firstName", source = "leaveEntitlement.employee.firstName")
	@Mapping(target = "employee.lastName", source = "leaveEntitlement.employee.lastName")
	CarryForwardDetailsResponseDto leaveEntitlementToCarryForwardDetailsDto(LeaveEntitlement leaveEntitlement);

	EntitlementDto customEntitlementDtoToEntitlementDto(CustomEntitlementDto customEntitlementDto);

	@Mapping(target = "validFrom", ignore = true)
	@Mapping(target = "validTo", ignore = true)
	@Mapping(target = "employee.firstName", source = "entitlementDetailsDto.employeeName")
	@Mapping(target = "leaveType.typeId", source = "entitlementDto.leaveTypeId")
	@Mapping(target = "leaveType.name", source = "entitlementDto.name")
	@Mapping(target = "totalDaysAllocated", source = "entitlementDto.totalDaysAllocated", defaultValue = "0F")
	@Mapping(target = "totalDaysUsed", expression = "java(0F)")
	LeaveEntitlement entitlementDetailsDtoToLeaveEntitlement(EntitlementDetailsDto entitlementDetailsDto,
			EntitlementDto entitlementDto);

	@Mapping(target = "leaveTypeId", source = "leaveType.typeId")
	@Mapping(target = "name", source = "leaveType.name")
	@Mapping(target = "totalDaysAllocated", source = "leaveEntitlement.totalDaysAllocated")
	@Mapping(target = "validFrom", source = "leaveEntitlement.validFrom")
	@Mapping(target = "validTo", source = "leaveEntitlement.validTo")
	CustomEntitlementDto leaveTypeAndEntitlementToEntitlementDto(LeaveType leaveType,
			LeaveEntitlement leaveEntitlement);

	@Mapping(source = "viewed", target = "isViewed")
	LeaveRequestResponseDto leaveRequestToLeaveRequestResponseDto(LeaveRequest leaveRequest);

	LeaveTypeBasicInfoDto leaveTypeToLeaveTypeBasicInfoDto(LeaveType leaveType);

	List<HolidayResponseDto> holidaysToHolidayResponseDtoList(List<Holiday> holidayList);

	EmployeeEntitlementTeamJobRoleDto employeeLeaveEntitlementTeamJobRoleToEmployeeEntitlementTeamJobRoleDto(
			EmployeeLeaveEntitlementReportExportDto etj);

	LeaveEntitlementReportDto leaveReportDtoToLeaveEntitlementReportDto(LeaveReportDto lrd);

	EmployeeLeaveReportResponseDto leaveReportDtoToEmployeeLeaveReportResponseDto(LeaveReportDto lrd);

	@Mapping(target = "leaveType.typeId", source = "leaveRequestDto.typeId")
	@Mapping(target = "attachments", ignore = true)
	LeaveRequest leaveRequestDtoToLeaveRequest(LeaveRequestDto leaveRequestDto);

	@Mapping(source = "viewed", target = "isViewed")
	LeaveRequestByIdResponseDto leaveRequestToLeaveRequestByIdResponseDto(LeaveRequest leaveRequest);

	LeaveType leaveTypeDtoToLeaveType(LeaveTypeRequestDto leaveTypeRequestDto);

	LeaveTypeRequestDto leaveTypeToLeaveTypeDto(LeaveType leaveType);

	List<LeaveTypeResponseDto> leaveTypeListToLeaveTypeResponseDtoList(List<LeaveType> leaveTypes);

	LeaveTypeResponseDto leaveTypeToLeaveTypeResponseDto(LeaveType leaveType);

	ClockInSummaryLeaveRequestResponseDto leaveRequestToClockInSummaryLeaveRequestResponseDto(
			LeaveRequest leaveRequest);

	List<AllLeaveRequestsResponseDto> leaveRequestListToAllLeaveRequestsResponseDtoList(
			List<LeaveRequest> leaveRequests);

	@IterableMapping(elementTargetType = LeaveRequestManagerResponseDto.class)
	List<LeaveRequestManagerResponseDto> leaveRequestListToLeaveRequestManagerResponseDtoList(
			List<LeaveRequest> leaveRequest);

	List<LeaveRequestWithEmployeeResponseDto> leaveRequestsToLeaveRequestWithEmployeeResponseDtos(
			List<LeaveRequest> leaveRequests);

	List<SummarizedCustomLeaveEntitlementDto> summarizedCustomLeaveEntitlementDto(
			List<LeaveEntitlement> leaveEntitlements);

	List<SummarizedLeaveEntitlementBalanceDto> leaveEntitlementsToSummarizedLeaveEntitlementBalanceDto(
			List<LeaveEntitlement> leaveEntitlements);

	LeaveTypeBasicDetailsResponseDto leaveTypeToLeaveTypeBasicDetailsResponseDto(LeaveType leaveType);

	LeaveRequestManagerResponseDto leaveRequestToLeaveRequestManagerResponseDto(LeaveRequest leaveRequest);

}
