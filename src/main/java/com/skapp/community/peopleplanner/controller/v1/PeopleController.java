package com.skapp.community.peopleplanner.controller.v1;

import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeBulkDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDataValidationDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeFilterDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeIsAvailableDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeQuickAddDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeUpdateDto;
import com.skapp.community.peopleplanner.payload.request.NotificationSettingsPatchRequestDto;
import com.skapp.community.peopleplanner.payload.request.PermissionFilterDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeManagerResponseDto;
import com.skapp.community.peopleplanner.service.EmployeeTimelineService;
import com.skapp.community.peopleplanner.service.PeopleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/people")
@Tag(name = "People Controller", description = "Endpoints for managing employees")
public class PeopleController {

	@NonNull
	private final PeopleService peopleService;

	@NonNull
	private final EmployeeTimelineService employeeService;

	@Operation(summary = "Create a new employee",
			description = "This endpoint creates a new employee with the provided details.")
	@PostMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_PEOPLE_ADMIN')")
	public ResponseEntity<ResponseEntityDto> addNewEmployee(@Valid @RequestBody EmployeeDetailsDto employeeDetailsDto) {
		ResponseEntityDto response = peopleService.addNewEmployee(employeeDetailsDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(summary = "Quick add a new employee",
			description = "This endpoint quickly adds a new employee with limited details.")
	@PostMapping(value = "/employee/quick-add", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_PEOPLE_ADMIN')")
	public ResponseEntity<ResponseEntityDto> addNewQuickAddEmployee(
			@Valid @RequestBody EmployeeQuickAddDto employeeQuickAddDto) {
		ResponseEntityDto response = peopleService.quickAddEmployee(employeeQuickAddDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(summary = "Update an employee", description = "This endpoint updates an existing employee by their ID.")
	@PatchMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_PEOPLE_ADMIN')")
	public ResponseEntity<ResponseEntityDto> updateEmployee(
			@PathVariable @Schema(description = "ID of the employee to update", example = "1") Long id,
			@Valid @RequestBody EmployeeUpdateDto employeeUpdateDto) {
		ResponseEntityDto response = peopleService.updateEmployee(id, employeeUpdateDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Update an employee", description = "This endpoint updates an existing employee by their ID.")
	@PatchMapping(value = "/employee/me/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_ATTENDANCE_EMPLOYEE','ROLE_PEOPLE_EMPLOYEE','ROLE_LEAVE_EMPLOYEE')")
	public ResponseEntity<ResponseEntityDto> updateCurrentEmployee(
			@PathVariable @Schema(description = "ID of the employee to update", example = "1") Long id,
			@Valid @RequestBody EmployeeUpdateDto employeeUpdateDto) {
		ResponseEntityDto response = peopleService.updateLoggedInUser(id, employeeUpdateDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Get a list of employees",
			description = "This endpoint fetches a list of employees based on provided filters.")
	@GetMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getEmployees(EmployeeFilterDto employeeFilterDto) {
		ResponseEntityDto response = peopleService.getEmployees(employeeFilterDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Get employee by ID", description = "This endpoint fetches an employee by their ID.")
	@GetMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getEmployeeById(@PathVariable Long id) {
		ResponseEntityDto employeeResponse = peopleService.getEmployeeById(id);
		return new ResponseEntity<>(employeeResponse, HttpStatus.OK);
	}

	@Operation(summary = "Get timeline records of an employee",
			description = "This endpoint fetches the timeline records of an employee by their ID.")
	@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_PEOPLE_ADMIN','ROLE_PEOPLE_MANAGER')")
	@GetMapping(value = "/employees/timeline/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> employeeTimelineRecords(@PathVariable Long id) {
		ResponseEntityDto response = employeeService.getEmployeeTimelineRecords(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Get current logged-in employee",
			description = "This endpoint fetches the current logged-in employee's details.")
	@GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getCurrentEmployee() {
		return new ResponseEntity<>(peopleService.getCurrentEmployee(), HttpStatus.OK);
	}

	@Operation(summary = "Get current logged-in employee managers",
			description = "This endpoint fetches the current logged-in employee's managers.")
	@GetMapping(value = "/me/managers")
	public ResponseEntity<ResponseEntityDto> getCurrentEmployeeManagers() {
		List<EmployeeManagerResponseDto> employeeManagers = peopleService.getCurrentEmployeeManagers();
		return new ResponseEntity<>(new ResponseEntityDto(false, employeeManagers), HttpStatus.OK);
	}

	@Operation(summary = "Check if current logged-in employee has managers",
			description = "This endpoint checks if there are any managers assigned to the current logged-in employee.")
	@GetMapping(value = "/me/managers/availability")
	public ResponseEntity<ResponseEntityDto> isManagerAvailableForCurrentEmployee() {
		boolean isManagerAvailable = peopleService.isManagerAvailableForCurrentEmployee();
		return new ResponseEntity<>(new ResponseEntityDto(false, isManagerAvailable), HttpStatus.OK);
	}

	@Operation(summary = "Bulk add employees", description = "This endpoint allows adding multiple employees at once.")
	@PostMapping(value = "/bulk/employees", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_PEOPLE_ADMIN')")
	public ResponseEntity<ResponseEntityDto> addBulkEmployees(@RequestBody List<EmployeeBulkDto> employeeBulkDto) {
		ResponseEntityDto response = peopleService.addBulkEmployees(employeeBulkDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping(value = "/pending-employee-count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getLoginPendingEmployeeCount() {
		ResponseEntityDto response = peopleService.getLoginPendingEmployeeCount();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/search/employee", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> searchEmployeesByNameOrEmail(
			@Valid PermissionFilterDto permissionFilterDto) {
		ResponseEntityDto response = peopleService.searchEmployeesByNameOrEmail(permissionFilterDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/search/email-exists", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> searchEmployeesByEmail(
			@Valid EmployeeIsAvailableDto employeeIsAvailableDto) {
		ResponseEntityDto response = peopleService.searchEmployeesByEmail(employeeIsAvailableDto.getEmail());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/check-email-identification-no", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getEmployeeByIdOrEmail(
			@Valid EmployeeDataValidationDto employeeDataValidationDto) {
		ResponseEntityDto response = peopleService.getEmployeeByIdOrEmail(employeeDataValidationDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Terminate an user", description = "Terminate an user account")
	@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_PEOPLE_ADMIN')")
	@PatchMapping("/user/terminate/{userId}")
	public ResponseEntity<ResponseEntityDto> terminateUser(@PathVariable Long userId) {
		ResponseEntityDto response = peopleService.terminateUser(userId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ROLE_PEOPLE_MANAGER','ROLE_ATTENDANCE_MANAGER','ROLE_LEAVE_MANAGER')")
	@PatchMapping("/user/notification/settings")
	public ResponseEntity<ResponseEntityDto> updateNotificationSettings(
			@RequestBody NotificationSettingsPatchRequestDto notificationSettingsPatchRequestDto) {
		ResponseEntityDto response = peopleService.updateNotificationSettings(notificationSettingsPatchRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/user/notification/settings")
	public ResponseEntity<ResponseEntityDto> getNotificationSettings() {
		ResponseEntityDto response = peopleService.getNotificationSettings();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ROLE_ATTENDANCE_MANAGER', 'ROLE_LEAVE_MANAGER')")
	@GetMapping(value = "/search/employee-team", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> searchEmployeesAndTeamsBySearchKeyword(@RequestParam String keyword) {
		ResponseEntityDto response = peopleService.searchEmployeesAndTeamsByKeyword(keyword);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
