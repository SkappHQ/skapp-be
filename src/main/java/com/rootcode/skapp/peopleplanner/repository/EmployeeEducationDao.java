package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.EmployeeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeEducationDao
		extends JpaRepository<EmployeeEducation, Long>, JpaSpecificationExecutor<EmployeeEducation> {

	Optional<EmployeeEducation> findByEducationId(Long item);

}
