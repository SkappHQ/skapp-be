package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.EmployeeTeam;
import com.rootcode.skapp.peopleplanner.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeTeamDao extends JpaRepository<EmployeeTeam, Long>, EmployeeTeamRepository {

	List<EmployeeTeam> findEmployeeTeamsByTeam(Team team);

	List<EmployeeTeam> findEmployeeTeamsByEmployee(Employee employee);

}
