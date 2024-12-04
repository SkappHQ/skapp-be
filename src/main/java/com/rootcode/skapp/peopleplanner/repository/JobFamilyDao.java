package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.JobFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobFamilyDao
		extends JpaRepository<JobFamily, Long>, JpaSpecificationExecutor<JobFamily>, JobFamilyRepository {

	Optional<JobFamily> findByJobFamilyIdAndIsActive(Long jobFamilyId, boolean isActive);

}
