package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.EmployeePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeePeriodDao extends JpaRepository<EmployeePeriod, Long> {

	Optional<EmployeePeriod> findEmployeePeriodByEmployee_EmployeeId(Long employeeId);

	Optional<EmployeePeriod> findEmployeePeriodByEmployee_EmployeeIdAndIsActiveTrue(Long employeeId);

}
