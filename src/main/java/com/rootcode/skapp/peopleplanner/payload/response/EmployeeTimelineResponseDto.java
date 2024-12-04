package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.type.EmployeeTimelineType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeTimelineResponseDto {

	private Long id;

	private EmployeeTimelineType timelineType;

	private String title;

	private String previousValue;

	private String newValue;

	private LocalDate displayDate;

	private String createdBy;

}
