package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.common.payload.response.BulkStatusSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class HolidayBulkSaveResponseDto {

	String message;

	BulkStatusSummary bulkStatusSummary;

	List<HolidayDtoStatusResponseDto> bulkRecordErrorLogs;

}
