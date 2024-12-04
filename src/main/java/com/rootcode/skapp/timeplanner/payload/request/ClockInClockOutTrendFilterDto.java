package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.timeplanner.type.RecordType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ClockInClockOutTrendFilterDto {

	private List<Long> teams;

	private RecordType recordType;

	private String timeOffset;

	private LocalDate date;

}
