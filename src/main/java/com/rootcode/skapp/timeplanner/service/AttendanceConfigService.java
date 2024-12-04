package com.rootcode.skapp.timeplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.timeplanner.payload.request.AttendanceConfigRequestDto;
import com.rootcode.skapp.timeplanner.type.AttendanceConfigType;

public interface AttendanceConfigService {

	void setDefaultAttendanceConfig();

	ResponseEntityDto updateAttendanceConfig(AttendanceConfigRequestDto attendanceConfigRequestDto);

	ResponseEntityDto getAllAttendanceConfigs();

	boolean getAttendanceConfigByType(AttendanceConfigType attendanceConfigType);

}
