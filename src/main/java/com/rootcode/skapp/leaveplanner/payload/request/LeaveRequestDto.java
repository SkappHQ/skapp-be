package com.rootcode.skapp.leaveplanner.payload.request;

import com.rootcode.skapp.leaveplanner.type.LeaveState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class LeaveRequestDto {

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	@NotNull
	private Long typeId;

	@NotNull
	private LeaveState leaveState;

	private String requestDesc;

	private List<String> attachments;

}
