package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.EmployeeTeam;
import com.rootcode.skapp.peopleplanner.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeamRepository {

	List<Long> findSupervisorIdsByTeamId(Long id);

	List<Long> findMemberIdsByTeamId(Long id);

	List<EmployeeTeam> findSupervisorTeamsByTeamId(Long teamId, List<Long> employeeId);

	List<EmployeeTeam> findMemberTeamsByTeamId(Long teamId, List<Long> employeeId);

	List<Long> findLeadingTeamIdsByManagerId(Long employeeId);

	List<Team> findLeadingTeamsByManagerId(Long employeeId);

	Page<Employee> findEmployeesInManagerLeadingTeams(List<Long> teamIds, Pageable page);

	List<Employee> findEmployeesInTeamByTeamId(Long teamId, Pageable page);

	List<Team> findTeamsManagedByUser(Long userId, boolean isActive);

	List<Employee> findEmployeesByTeamIds(List<Long> teamIds);

	List<Team> findTeamsByName(String keyword);

	void deleteTeamById(Long teamId);

}
