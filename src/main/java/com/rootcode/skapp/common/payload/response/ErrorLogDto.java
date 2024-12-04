package com.rootcode.skapp.common.payload.response;

import com.rootcode.skapp.common.type.BulkItemStatus;
import com.rootcode.skapp.leaveplanner.payload.CustomEntitlementDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogDto {

	private String email;

	private Long employeeId;

	private String employeeName;

	private List<CustomEntitlementDto> entitlementsDto;

	private BulkItemStatus status;

	private String message;

}
