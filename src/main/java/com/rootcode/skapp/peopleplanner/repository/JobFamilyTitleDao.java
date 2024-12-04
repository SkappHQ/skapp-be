package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.JobFamilyTitle;
import com.rootcode.skapp.peopleplanner.model.JobFamilyTitleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobFamilyTitleDao
		extends JpaRepository<JobFamilyTitle, JobFamilyTitleId>, JpaSpecificationExecutor<JobFamilyTitle> {

}
