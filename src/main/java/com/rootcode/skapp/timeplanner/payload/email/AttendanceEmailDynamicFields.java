package com.rootcode.skapp.timeplanner.payload.email;

import com.rootcode.skapp.common.payload.email.CommonEmailDynamicFields;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceEmailDynamicFields extends CommonEmailDynamicFields {

	private String timeEntryDate;

	private String startTime;

	private String endTime;

	private String nonWorkingDates;

}
