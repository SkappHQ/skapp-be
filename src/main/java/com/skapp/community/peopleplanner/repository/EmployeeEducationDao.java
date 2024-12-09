package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.EmployeeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeEducationDao
		extends JpaRepository<EmployeeEducation, Long>, JpaSpecificationExecutor<EmployeeEducation> {

	Optional<EmployeeEducation> findByEducationId(Long item);

}
