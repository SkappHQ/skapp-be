package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.JobFamily;
import com.rootcode.skapp.peopleplanner.model.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao
		extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>, EmployeeRepository {

	List<Employee> findByJobFamilyAndJobTitle(JobFamily jobFamily, JobTitle jobTitle);

	Optional<Employee> findByEmployeeId(Long employeeId);

	List<Employee> findByIdentificationNo(String identificationNo);

	Employee findEmployeeByEmployeeIdAndUserIsActiveTrue(Long primaryManager);

	Employee getEmployeeByEmployeeId(long employeeId);

}
