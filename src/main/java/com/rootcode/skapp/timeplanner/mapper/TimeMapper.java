package com.rootcode.skapp.timeplanner.mapper;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.type.RequestStatus;
import com.rootcode.skapp.timeplanner.model.TimeConfig;
import com.rootcode.skapp.timeplanner.model.TimeRecord;
import com.rootcode.skapp.timeplanner.model.TimeRequest;
import com.rootcode.skapp.timeplanner.model.TimeSlot;
import com.rootcode.skapp.timeplanner.payload.projection.TimeRecordsByEmployeesDto;
import com.rootcode.skapp.timeplanner.payload.request.ManagerTimeRequestResponseDto;
import com.rootcode.skapp.timeplanner.payload.request.TimeRequestDto;
import com.rootcode.skapp.timeplanner.payload.response.EmployeeTimeRequestResponseDto;
import com.rootcode.skapp.timeplanner.payload.response.TimeConfigResponseDto;
import com.rootcode.skapp.timeplanner.payload.response.TimeRecordChipResponseDto;
import com.rootcode.skapp.timeplanner.payload.response.TimeRecordResponseDto;
import com.rootcode.skapp.timeplanner.type.SlotType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeMapper {

	List<EmployeeTimeRequestResponseDto> timeRequestListToTimeRequestResponseDtoList(List<TimeRequest> content);

	EmployeeTimeRequestResponseDto timeRequestToTimeRequestResponseDto(TimeRequest editTimeRequest);

	TimeRecordResponseDto timeRecordToTimeRecordResponseDto(TimeRecord timeRecord);

	@Mapping(target = "clockInTime", source = "startTime")
	@Mapping(target = "day", source = "day")
	@Mapping(target = "employee", source = "employee")
	@Mapping(target = "date", source = "date")
	TimeRecord newTimeRecordToTimeRecord(Employee employee, long startTime, DayOfWeek day, LocalDate date);

	@Mapping(target = "activeRightNow", source = "isActiveRightNow")
	@Mapping(target = "startTime", source = "startTime")
	@Mapping(target = "timeRecord", source = "timeRecord")
	@Mapping(target = "slotType", source = "slotType")
	@Mapping(target = "manualEntry", source = "manualEntry")
	TimeSlot newTimeSlotToTimeSlot(Long startTime, SlotType slotType, boolean isActiveRightNow, TimeRecord timeRecord,
			boolean manualEntry);

	@Mapping(target = "status", source = "requestStatus")
	@Mapping(target = "employee", source = "employee")
	@Mapping(target = "timeRecord", source = "timeRecord")
	@Mapping(target = "initialClockIn", source = "clockInTime")
	@Mapping(target = "initialClockOut", source = "clockOutTime")
	@Mapping(target = "requestedStartTime", source = "requestedStartTime")
	@Mapping(target = "requestedEndTime", source = "requestedEndTime")
	@Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now(java.time.ZoneOffset.UTC))")
	TimeRequest timeRequestDtoToTimeRequest(TimeRequestDto timeRequestDto, RequestStatus requestStatus,
			Employee employee, TimeRecord timeRecord, Long clockInTime, Long clockOutTime, Long requestedStartTime,
			Long requestedEndTime);

	@Mapping(target = "startTime", expression = "java(mapToLocalTime(tcm.getStartHour(), tcm.getStartMinute()))")
	TimeConfigResponseDto timeConfigToTimeConfigResponseDto(TimeConfig tcm);

	default LocalTime mapToLocalTime(int hour, int minute) {
		return LocalTime.of(hour, minute);
	}

	@Mapping(target = "clockInTime", source = "timeRequest.requestedStartTime")
	@Mapping(target = "clockOutTime", source = "timeRequest.requestedEndTime")
	@Mapping(target = "day", source = "day")
	@Mapping(target = "employee", source = "employee")
	@Mapping(target = "date", source = "date")
	@Mapping(target = "workedHours", expression = "java(0f)")
	@Mapping(target = "breakHours", expression = "java(0f)")
	@Mapping(target = "leaveHours", expression = "java(0f)")
	@Mapping(target = "createdBy", expression = "java(\"admin\")")
	@Mapping(target = "lastModifiedBy", expression = "java(\"admin\")")
	@Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now(java.time.ZoneOffset.UTC))")
	TimeRecord buildNewTimeRecord(Employee employee, TimeRequest timeRequest, DayOfWeek day, LocalDate date);

	List<ManagerTimeRequestResponseDto> timeRequestListToManagerTimeRequestResponseDtoList(
			List<TimeRequest> timeRequestList);

	TimeRecordChipResponseDto timeRecordsByEmployeeToTimeRecordRowResponseDto(
			TimeRecordsByEmployeesDto timeRecordsByEmployeesDto);

}
