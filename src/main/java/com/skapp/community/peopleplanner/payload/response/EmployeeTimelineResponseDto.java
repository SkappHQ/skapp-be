package com.skapp.community.peopleplanner.payload.response;

import com.skapp.community.peopleplanner.type.EmployeeTimelineType;
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
