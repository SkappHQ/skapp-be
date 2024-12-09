package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.EmployeePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeePeriodDao extends JpaRepository<EmployeePeriod, Long> {

	Optional<EmployeePeriod> findEmployeePeriodByEmployee_EmployeeId(Long employeeId);

	Optional<EmployeePeriod> findEmployeePeriodByEmployee_EmployeeIdAndIsActiveTrue(Long employeeId);

}
