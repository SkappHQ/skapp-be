package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeManagerDao extends JpaRepository<EmployeeManager, Long>, EmployeeManagerRepository {

	List<EmployeeManager> findByEmployee(Employee employee);

	boolean existsByEmployee(Employee employee);

}
