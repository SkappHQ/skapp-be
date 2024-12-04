package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Holiday;
import com.rootcode.skapp.peopleplanner.payload.request.HolidayFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepository {

	Page<Holiday> findAllHolidays(HolidayFilterDto holidayFilterDto, Pageable pageable);

}
