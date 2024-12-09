package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.payload.response.JobFamilyOverviewDto;
import com.skapp.community.peopleplanner.payload.response.JobTitleOverviewDto;

import java.util.List;

public interface JobFamilyRepository {

	List<JobFamily> getJobFamiliesByEmployeeCount();

	List<JobFamilyOverviewDto> getJobFamilyOverview(List<Long> teamIds);

	List<JobTitleOverviewDto> getJobTitlesByJobFamily(Long jobFamilyId);

}
