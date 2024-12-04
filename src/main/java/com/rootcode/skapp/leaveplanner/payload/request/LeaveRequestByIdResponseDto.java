package com.rootcode.skapp.leaveplanner.payload.request;

import com.rootcode.skapp.leaveplanner.payload.response.LeaveRequestResponseDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LeaveRequestByIdResponseDto extends LeaveRequestResponseDto {

	private String requestDesc;

	private String reviewerComment;

	private LocalDateTime reviewedDate;

	private EmployeeBasicDetailsResponseDto employee;

	private EmployeeBasicDetailsResponseDto reviewer;

	private Boolean isViewed;

	private LocalDateTime createdDate;

	private List<LeaveRequestAttachmentDto> attachments;

}
