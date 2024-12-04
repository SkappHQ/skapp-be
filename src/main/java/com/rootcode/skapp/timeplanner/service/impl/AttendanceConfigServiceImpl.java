package com.rootcode.skapp.timeplanner.service.impl;

import com.rootcode.skapp.common.exception.ModuleException;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.common.util.MessageUtil;
import com.rootcode.skapp.timeplanner.constant.TimeMessageConstant;
import com.rootcode.skapp.timeplanner.model.AttendanceConfig;
import com.rootcode.skapp.timeplanner.payload.request.AttendanceConfigRequestDto;
import com.rootcode.skapp.timeplanner.repository.AttendanceConfigDao;
import com.rootcode.skapp.timeplanner.service.AttendanceConfigService;
import com.rootcode.skapp.timeplanner.type.AttendanceConfigType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceConfigServiceImpl implements AttendanceConfigService {

	@NonNull
	private final AttendanceConfigDao attendanceConfigDao;

	@NonNull
	private final MessageUtil messageUtil;

	public static final String DEFAULT_CONFIG_VALUE = "false";

	@Override
	public void setDefaultAttendanceConfig() {
		log.info("setDefaultAttendanceConfig: execution started");

		Map<AttendanceConfigType, String> configMap = new EnumMap<>(AttendanceConfigType.class);
		configMap.put(AttendanceConfigType.CLOCK_IN_ON_NON_WORKING_DAYS, DEFAULT_CONFIG_VALUE);
		configMap.put(AttendanceConfigType.CLOCK_IN_ON_COMPANY_HOLIDAYS, DEFAULT_CONFIG_VALUE);
		configMap.put(AttendanceConfigType.CLOCK_IN_ON_LEAVE_DAYS, DEFAULT_CONFIG_VALUE);
		configMap.put(AttendanceConfigType.AUTO_APPROVAL_FOR_CHANGES, DEFAULT_CONFIG_VALUE);

		configMap.forEach(this::updateOrCreateConfig);

		log.info("setDefaultAttendanceConfig: attendance configuration successfully created");
	}

	@Override
	@Transactional
	public ResponseEntityDto updateAttendanceConfig(AttendanceConfigRequestDto attendanceConfigRequestDto) {
		log.info("updateAttendanceConfig: execution started");

		Map<AttendanceConfigType, String> configMap = new EnumMap<>(AttendanceConfigType.class);
		configMap.put(AttendanceConfigType.CLOCK_IN_ON_NON_WORKING_DAYS,
				String.valueOf(attendanceConfigRequestDto.getIsClockInOnNonWorkingDays()));
		configMap.put(AttendanceConfigType.CLOCK_IN_ON_COMPANY_HOLIDAYS,
				String.valueOf(attendanceConfigRequestDto.getIsClockInOnCompanyHolidays()));
		configMap.put(AttendanceConfigType.CLOCK_IN_ON_LEAVE_DAYS,
				String.valueOf(attendanceConfigRequestDto.getIsClockInOnLeaveDays()));
		configMap.put(AttendanceConfigType.AUTO_APPROVAL_FOR_CHANGES,
				String.valueOf(attendanceConfigRequestDto.getIsAutoApprovalForChanges()));

		configMap.forEach(this::updateOrCreateConfig);

		log.info("updateAttendanceConfig: execution ended");
		return new ResponseEntityDto(messageUtil.getMessage(TimeMessageConstant.TIME_SUCCESS_ATTENDANCE_CONFIG_UPDATED),
				false);
	}

	private void updateOrCreateConfig(AttendanceConfigType configType, String configValue) {
		AttendanceConfig config = attendanceConfigDao.findByAttendanceConfigType(configType);
		if (config == null) {
			config = new AttendanceConfig(configType, configValue);
		}
		else {
			config.setAttendanceConfigValue(configValue);
		}

		attendanceConfigDao.save(config);
	}

	@Override
	public ResponseEntityDto getAllAttendanceConfigs() {
		List<AttendanceConfig> attendanceConfigs = attendanceConfigDao.findAll();

		AttendanceConfigRequestDto dto = new AttendanceConfigRequestDto(false, false, false, false);

		for (AttendanceConfig config : attendanceConfigs) {
			boolean value = Boolean.parseBoolean(config.getAttendanceConfigValue());
			switch (config.getAttendanceConfigType()) {
				case CLOCK_IN_ON_NON_WORKING_DAYS -> dto.setIsClockInOnNonWorkingDays(value);
				case CLOCK_IN_ON_COMPANY_HOLIDAYS -> dto.setIsClockInOnCompanyHolidays(value);
				case CLOCK_IN_ON_LEAVE_DAYS -> dto.setIsClockInOnLeaveDays(value);
				case AUTO_APPROVAL_FOR_CHANGES -> dto.setIsAutoApprovalForChanges(value);
			}
		}

		return new ResponseEntityDto(false, dto);
	}

	@Override
	public boolean getAttendanceConfigByType(AttendanceConfigType attendanceConfigType) {
		AttendanceConfig config = attendanceConfigDao.findByAttendanceConfigType(attendanceConfigType);
		if (config == null) {
			throw new ModuleException(TimeMessageConstant.TIME_ERROR_ATTENDANCE_CONFIG_NOT_FOUND);
		}
		return Boolean.parseBoolean(config.getAttendanceConfigValue());
	}

}
