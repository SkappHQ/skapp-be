package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.Team;
import com.rootcode.skapp.timeplanner.type.ClockInType;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeTeamRepository {

	Long countAvailableEmployeesByTeamIdsAndDate(List<Long> teamsFilter, LocalDate currentDate, Long currentUserId);

	List<Employee> getEmployeesByTeamIds(String searchKeyword, List<Long> teams, List<ClockInType> clockInType,
			LocalDate date, Long currentUserId);

	List<Team> findTeamsByEmployeeId(Long employeeId);

	List<Employee> getEmployeesByTeamIds(List<Long> teams, Long currentUserId, boolean isAdmin);

	void deleteEmployeeTeamByTeamId(Long teamId);

	void deleteEmployeeTeamByTeamIdAndEmployeeIds(Long teamId, List<Long> employeeIds);

}
