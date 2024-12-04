package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.peopleplanner.type.HolidayDuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HolidayBasicDetailsResponseDto {

	private Long id;

	private String name;

	private HolidayDuration holidayDuration;

}
