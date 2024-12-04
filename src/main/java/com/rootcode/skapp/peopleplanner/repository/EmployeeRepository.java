package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.leaveplanner.payload.AdminOnLeaveDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeLeaveRequestDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeesOnLeaveFilterDto;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeFilterDto;
import com.rootcode.skapp.peopleplanner.payload.request.PermissionFilterDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeCountDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeManagerDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeTeamDto;
import com.rootcode.skapp.peopleplanner.type.EmployeeType;
import com.rootcode.skapp.peopleplanner.type.EmploymentAllocation;
import com.rootcode.skapp.peopleplanner.type.Gender;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

	Optional<Employee> findEmployeeByEmployeeIdAndUserActiveNot(Long employeeId, boolean userState);

	Page<Employee> findEmployees(EmployeeFilterDto employeeFilterDto, Pageable page);

	List<EmployeeTeamDto> findTeamsByEmployees(List<Long> employeeIds);

	List<EmployeeManagerDto> findManagersByEmployeeIds(List<Long> employeeId);

	EmployeeCountDto getLoginPendingEmployeeCount();

	List<Employee> findEmployeeByNameEmail(String search, PermissionFilterDto permissionFilterDto);

	Employee findEmployeeByEmail(String email);

	Page<Employee> findEmployeesByManagerId(Long managerId, Pageable page);

	AdminOnLeaveDto findAllEmployeesOnLeave(EmployeesOnLeaveFilterDto filterDto, Long currentUserId,
			boolean isLeaveAdmin);

	List<Long> findEmployeeIdsByManagerId(Long employeeId);

	Long findAllActiveEmployeesCount();

	List<Employee> findManagersByEmployeeIdAndLoggedInManagerId(@NonNull Long id, Long employeeId);

	List<EmployeeLeaveRequestDto> getEmployeesOnLeaveByTeam(EmployeesOnLeaveFilterDto filterDto, Long currentUserId);

	List<Employee> findInformantManagersByEmployeeID(Long employeeId);

	Long countByIsActiveAndTeams(List<Long> teamIds);

	Long countByIsActiveAndTeamsAndCreatedAt(boolean isActive, List<Long> teamIds, int currentYear);

	Long findTotalAgeOfActiveEmployeesByTeamIds(Long teamId);

	Double findAverageAgeOfActiveEmployeesByTeamIds(List<Long> teamIds);

	Long countTerminatedEmployeesByStartDateAndEndDateAndTeams(LocalDate startDate, LocalDate endDate,
			List<Long> teamId);

	Long countByCreateDateRangeAndTeams(LocalDate endDate, List<Long> teamId);

	Long countByIsActiveAndTeamsAndGender(boolean isActive, List<Long> teamIds, Gender gender);

	Long countByEmploymentTypeAndEmploymentAllocationAndTeams(EmployeeType employmentType,
			EmploymentAllocation employmentAllocation, List<Long> teamIds);

	List<Employee> findByNameAndIsActiveAndTeam(String employeeName, Boolean isActive, Long teamId);

	Long countEmployeesByManagerId(Long managerId);

	Long countAllAvailableEmployeesLeavesByDate(LocalDate currentDate);

	List<Employee> findEmployeeByName(String keyword);

	List<Employee> findEmployeesByTeams(List<Long> teamIds);

}
