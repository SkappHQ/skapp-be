package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.type.OrganizationalLeaveAnalyticsKPIType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LeaveAnalyticsFilterDto {

	@Nullable
	private OrganizationalLeaveAnalyticsKPIType analyticsType;

	private List<OrganizationalLeaveAnalyticsKPIDto> organizationalLeaveAnalyticsDto;

}
