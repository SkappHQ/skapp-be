package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.EmployeeManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeManagerDao extends JpaRepository<EmployeeManager, Long>, EmployeeManagerRepository {

	List<EmployeeManager> findByEmployee(Employee employee);

	boolean existsByEmployee(Employee employee);

}
