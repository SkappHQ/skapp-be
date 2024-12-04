package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.type.OrganizationalLeaveAnalyticsKPIAbsenceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationalAbsenceRateAnalyticsDto {

	private OrganizationalLeaveAnalyticsKPIAbsenceType type;

	private Float currentAbsenceRate;

	private Float monthBeforeAbsenceRate;

}
