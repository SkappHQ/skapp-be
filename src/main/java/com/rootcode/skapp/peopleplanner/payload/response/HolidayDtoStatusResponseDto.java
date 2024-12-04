package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.HolidayRequestDto;
import com.rootcode.skapp.peopleplanner.type.BulkRecordStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class HolidayDtoStatusResponseDto {

	BulkRecordStatus status;

	String errorMessage;

	HolidayRequestDto holiday;

}
