package com.rootcode.skapp.common.payload.response;

import com.rootcode.skapp.common.type.BulkItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkRecordsResponseDto {

	private String email;

	private Long employeeId;

	private String employeeName;

	private List<Object> entitlementsDto;

	private BulkItemStatus status;

	private String message;

}
