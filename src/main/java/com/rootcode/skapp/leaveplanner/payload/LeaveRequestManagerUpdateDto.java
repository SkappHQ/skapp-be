package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.leaveplanner.type.LeaveRequestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRequestManagerUpdateDto {

	private LeaveRequestStatus status;

	private String reviewerComment;

}
