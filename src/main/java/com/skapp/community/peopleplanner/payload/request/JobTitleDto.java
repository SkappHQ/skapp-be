package com.skapp.community.peopleplanner.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class JobTitleDto {

	private Long jobTitleId;

	@NonNull
	@NotEmpty
	private String name;

}
