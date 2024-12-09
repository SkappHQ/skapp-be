package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.EmployeeProgression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeProgressionDao
		extends JpaRepository<EmployeeProgression, Long>, JpaSpecificationExecutor<EmployeeProgression> {

	Optional<EmployeeProgression> findByProgressionId(Long item);

}
