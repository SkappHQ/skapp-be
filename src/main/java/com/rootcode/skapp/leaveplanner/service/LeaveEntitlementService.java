package com.rootcode.skapp.leaveplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.leaveplanner.payload.BulkLeaveEntitlementDto;
import com.rootcode.skapp.leaveplanner.payload.CarryForwardLeaveTypesFilterDto;
import com.rootcode.skapp.leaveplanner.payload.CustomEntitlementsFilterDto;
import com.rootcode.skapp.leaveplanner.payload.CustomLeaveEntitlementDto;
import com.rootcode.skapp.leaveplanner.payload.CustomLeaveEntitlementPatchRequestDto;
import com.rootcode.skapp.leaveplanner.payload.CustomLeaveEntitlementsFilterDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementPatchRequestDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementsDto;
import com.rootcode.skapp.leaveplanner.payload.LeaveEntitlementsFilterDto;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.type.EmployeeTimelineType;

import java.util.List;

public interface LeaveEntitlementService {

	String processLeaveEntitlements(LeaveEntitlementsDto leaveEntitlementsDto);

	void updateLeaveEntitlements(Long id, LeaveEntitlementPatchRequestDto leaveEntitlementPatchRequestDto);

	void updateCustomLeaveEntitlements(Long id,
			CustomLeaveEntitlementPatchRequestDto customLeaveEntitlementPatchRequestDto);

	ResponseEntityDto deleteCustomLeaveEntitlements(Long id);

	void addNewEmployeeLeaveEntitlementTimelineRecord(Employee employee, EmployeeTimelineType timelineType,
			String title, String previousValue, String newValue);

	ResponseEntityDto deleteDefaultEntitlements(Long id);

	ResponseEntityDto createCustomEntitlementForEmployee(CustomLeaveEntitlementDto customEntitlementDto);

	ResponseEntityDto addLeaveEntitlements(LeaveEntitlementsDto leaveEntitlementsDto);

	ResponseEntityDto getLeaveEntitlementById(Long id);

	ResponseEntityDto getCustomLeaveEntitlementById(Long id);

	ResponseEntityDto forceCarryForwardEntitlements(List<Long> leaveTypes, Integer cycleStartYear);

	ResponseEntityDto getCarryForwardEntitlements(CarryForwardLeaveTypesFilterDto carryForwardLeaveTypesFilterDto);

	ResponseEntityDto getAllCustomLeaveEntitlements(CustomEntitlementsFilterDto customEntitlementsFilterDto);

	ResponseEntityDto addBulkNewLeaveEntitlement(BulkLeaveEntitlementDto bulkLeaveEntitlementDto);

	ResponseEntityDto getLeaveEntitlementByDate(CustomLeaveEntitlementsFilterDto customLeaveEntitlementsFilterDto);

	ResponseEntityDto getCurrentUserLeaveEntitlements(LeaveEntitlementsFilterDto leaveEntitlementsFilterDto);

	ResponseEntityDto getCurrentUserLeaveEntitlementBalance(Long id);

}
