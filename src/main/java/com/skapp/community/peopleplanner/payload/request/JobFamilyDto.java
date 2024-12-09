package com.skapp.community.peopleplanner.payload.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobFamilyDto {

	@NonNull
	private String name;

	@NonNull
	private List<String> titles;

}
