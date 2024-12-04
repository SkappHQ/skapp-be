package com.rootcode.skapp.leaveplanner.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EntitlementDetailsDto {

	private String employeeName;

	private String email;

	private Long employeeId;

	private List<CustomEntitlementDto> entitlements;

}
