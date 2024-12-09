package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeTeam;
import com.skapp.community.peopleplanner.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeTeamDao extends JpaRepository<EmployeeTeam, Long>, EmployeeTeamRepository {

	List<EmployeeTeam> findEmployeeTeamsByTeam(Team team);

	List<EmployeeTeam> findEmployeeTeamsByEmployee(Employee employee);

}
