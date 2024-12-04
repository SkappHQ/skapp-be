package com.rootcode.skapp.timeplanner.payload.response;

import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import com.rootcode.skapp.peopleplanner.type.RequestStatus;
import com.rootcode.skapp.peopleplanner.type.RequestType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeTimeRequestResponseDto {

	private Long timeRequestId;

	private RequestType requestType;

	private Long requestedStartTime;

	private Long requestedEndTime;

	private Long initialClockIn;

	private Long initialClockOut;

	private Double workHours;

	private RequestStatus status;

	private TimeRecordParentDto timeRecord;

	private EmployeeBasicDetailsResponseDto employee;

}
