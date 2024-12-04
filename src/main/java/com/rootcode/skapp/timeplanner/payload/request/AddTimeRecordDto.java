package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.common.util.DateTimeUtils;
import com.rootcode.skapp.timeplanner.type.TimeRecordActionTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddTimeRecordDto {

	private LocalDateTime time = DateTimeUtils.getCurrentUtcDateTime();

	private TimeRecordActionTypes recordActionType;

}
