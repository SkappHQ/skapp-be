package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.EmployeeFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeFamilyDao
		extends JpaRepository<EmployeeFamily, Long>, JpaSpecificationExecutor<EmployeeFamily> {

	Optional<EmployeeFamily> findByFamilyId(Long item);

}
