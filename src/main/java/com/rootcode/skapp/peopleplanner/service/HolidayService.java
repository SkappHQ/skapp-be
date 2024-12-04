package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.peopleplanner.payload.request.HolidayBulkRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.HolidayFilterDto;
import com.rootcode.skapp.peopleplanner.payload.request.HolidaysDeleteRequestDto;

import java.time.LocalDate;

public interface HolidayService {

	ResponseEntityDto getAllHolidays(HolidayFilterDto holidayFilterDto);

	ResponseEntityDto saveBulkHolidays(HolidayBulkRequestDto holidayBulkRequestDto);

	ResponseEntityDto getHolidaysByDate(LocalDate date);

	ResponseEntityDto deleteAllHolidays(int year);

	ResponseEntityDto deleteSelectedHolidays(HolidaysDeleteRequestDto holidayDeleteDto);

}
