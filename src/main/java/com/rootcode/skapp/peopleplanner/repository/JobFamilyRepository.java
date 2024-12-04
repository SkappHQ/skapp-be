package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.JobFamily;
import com.rootcode.skapp.peopleplanner.payload.response.JobFamilyOverviewDto;
import com.rootcode.skapp.peopleplanner.payload.response.JobTitleOverviewDto;

import java.util.List;

public interface JobFamilyRepository {

	List<JobFamily> getJobFamiliesByEmployeeCount();

	List<JobFamilyOverviewDto> getJobFamilyOverview(List<Long> teamIds);

	List<JobTitleOverviewDto> getJobTitlesByJobFamily(Long jobFamilyId);

}
