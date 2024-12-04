package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestSort;
import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;
import com.rootcode.skapp.leaveplanner.type.ManagerType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LeaveRequestFilterDto {

	@Min(0)
	private int page = 0;

	@Min(1)
	private int size = 10;

	private LeaveRequestSort sortKey = LeaveRequestSort.START_DATE;

	private Sort.Direction sortOrder = Sort.Direction.DESC;

	private List<LeaveRequestStatus> status;

	private List<Long> leaveType;

	private List<ManagerType> managerType = new ArrayList<>();

	private List<Long> teamIds;

	private LocalDate startDate;

	private LocalDate endDate;

	private String searchKeyword;

}
