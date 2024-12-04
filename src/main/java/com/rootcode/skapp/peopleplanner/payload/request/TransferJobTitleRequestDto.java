package com.rootcode.skapp.peopleplanner.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferJobTitleRequestDto {

	private Long employeeId;

	private Long jobTitleId;

}
