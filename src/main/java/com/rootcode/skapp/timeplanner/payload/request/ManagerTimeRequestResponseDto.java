package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.peopleplanner.payload.request.EmployeeDto;
import com.rootcode.skapp.peopleplanner.type.RequestStatus;
import com.rootcode.skapp.peopleplanner.type.RequestType;
import com.rootcode.skapp.timeplanner.payload.response.TimeRecordParentDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerTimeRequestResponseDto {

	@NotNull
	private Long timeRequestId;

	// ClockIn & ClockOut Time Will be only use in edit time requests
	private Long initialClockIn;

	private Long initialClockOut;

	@NotNull
	private Long requestedStartTime;

	@NotNull
	private Long requestedEndTime;

	@NotNull
	private RequestType requestType;

	@NotNull
	private RequestStatus status;

	private double workHours;

	@NotNull
	private EmployeeDto employee;

	private TimeRecordParentDto timeRecord;

}
