package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobTitleDao
		extends JpaRepository<JobTitle, Long>, JpaSpecificationExecutor<JobTitle>, JobFamilyRepository {

	Optional<JobTitle> findByJobTitleIdAndIsActive(Long jobTitleId, Boolean isActive);

}
