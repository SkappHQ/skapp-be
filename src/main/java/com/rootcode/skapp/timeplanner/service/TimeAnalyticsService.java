package com.rootcode.skapp.timeplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.timeplanner.payload.request.AttendanceDashboardSummaryFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.AverageHoursWorkedTrendFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.ClockInClockOutTrendFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.ClockInSummaryFilterDto;
import com.rootcode.skapp.timeplanner.payload.request.LateArrivalTrendFilterDto;

public interface TimeAnalyticsService {

	ResponseEntityDto getClockInClockOutTrend(ClockInClockOutTrendFilterDto clockInClockOutTrendFilterDto);

	ResponseEntityDto lateArrivalTrend(LateArrivalTrendFilterDto lateArrivalTrendFilterDto);

	ResponseEntityDto averageHoursWorkedTrend(AverageHoursWorkedTrendFilterDto averageHoursWorkedTrendFilterDto);

	ResponseEntityDto attendanceDashboardSummary(
			AttendanceDashboardSummaryFilterDto attendanceDashboardSummaryFilterDto);

	ResponseEntityDto clockInSummary(ClockInSummaryFilterDto clockInSummaryFilterDto);

	ResponseEntityDto getIndividualWorkUtilization(Long id);

	ResponseEntityDto averageEmployeeHoursWorkedTrend(AverageHoursWorkedTrendFilterDto averageHoursWorkedTrendFilterDto,
			Long employeeId);

}
