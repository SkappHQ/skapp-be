package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmploymentBreakdownFilterDto;
import com.rootcode.skapp.peopleplanner.payload.request.PeopleAnalyticsFilterDto;
import com.rootcode.skapp.peopleplanner.payload.request.PeopleAnalyticsPeopleFilterDto;

public interface PeopleAnalyticsService {

	ResponseEntityDto getDashBoardSummary(PeopleAnalyticsFilterDto peopleAnalyticsService);

	ResponseEntityDto getGenderDistribution(PeopleAnalyticsFilterDto peopleAnalyticsFilterDto);

	ResponseEntityDto getEmploymentBreakdown(EmploymentBreakdownFilterDto employmentBreakdownFilterDto);

	ResponseEntityDto getJobFamilyOverview(PeopleAnalyticsFilterDto peopleAnalyticsFilterDto);

	ResponseEntityDto getPeopleSection(PeopleAnalyticsPeopleFilterDto peopleAnalyticsPeopleFilterDto);

}
