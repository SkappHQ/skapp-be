package com.rootcode.skapp.leaveplanner.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerTeamResponseDto extends ManagerSummarizedTeamResponseDto {

	private Long onLeaveCount;

	private Long onlineCount;

	private Boolean isNonWorkingDay;

}
