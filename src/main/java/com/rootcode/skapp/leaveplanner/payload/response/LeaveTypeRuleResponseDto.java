package com.rootcode.skapp.leaveplanner.payload.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.rootcode.skapp.leaveplanner.model.LeaveType;
import com.rootcode.skapp.leaveplanner.type.GainEligibilityType;
import com.rootcode.skapp.leaveplanner.type.LeaveRuleCategory;
import com.rootcode.skapp.leaveplanner.type.LoseEligibilityType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveTypeRuleResponseDto {

	private Long ruleId;

	private String name;

	private LeaveRuleCategory leaveRuleCategory;

	private LeaveType leaveType;

	private GainEligibilityType gainEligibilityType;

	private LoseEligibilityType loseEligibilityType;

	private int earnDays;

	private JsonNode earnDaysGrid;

	private LocalDate validFrom;

	private LocalDate validTo;

}
