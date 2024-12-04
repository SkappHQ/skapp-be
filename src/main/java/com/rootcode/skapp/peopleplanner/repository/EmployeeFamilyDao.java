package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.EmployeeFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeFamilyDao
		extends JpaRepository<EmployeeFamily, Long>, JpaSpecificationExecutor<EmployeeFamily> {

	Optional<EmployeeFamily> findByFamilyId(Long item);

}
