package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.type.OrganizationalLeaveAnalyticsKPIAbsenceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrganizationalLeaveAnalyticsKPIDto {

	private OrganizationalLeaveAnalyticsKPIAbsenceType type;

	private Float absenceRate;

	private String dateRange;

}
