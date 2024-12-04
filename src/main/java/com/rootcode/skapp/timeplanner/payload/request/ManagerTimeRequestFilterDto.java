package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.peopleplanner.type.RequestStatus;
import com.rootcode.skapp.peopleplanner.type.RequestType;
import com.rootcode.skapp.timeplanner.type.TimeRequestSort;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ManagerTimeRequestFilterDto {

	private RequestType requestType;

	private List<RequestStatus> status;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	private int pageNumber = 0;

	private int pageSize = 10;

	private Sort.Direction sortBy = Sort.Direction.ASC;

	private TimeRequestSort sortKey = TimeRequestSort.REQUESTED_START_TIME;

	private Boolean isExport = false;

}
