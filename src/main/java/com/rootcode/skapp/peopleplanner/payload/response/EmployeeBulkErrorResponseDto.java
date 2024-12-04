package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.payload.response.BulkStatusSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeBulkErrorResponseDto {

	List<EmployeeBulkResponseDto> bulkRecordErrorLogs;

	BulkStatusSummary bulkStatusSummary;

}
