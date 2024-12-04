package com.rootcode.skapp.peopleplanner.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HolidaysDeleteRequestDto {

	private List<Long> holidayIds;

}
