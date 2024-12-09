package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface HolidayDao extends JpaRepository<Holiday, Long>, JpaSpecificationExecutor<Holiday>, HolidayRepository {

	List<Holiday> findAllByIsActiveTrueAndDate(LocalDate date);

	long countByIsActiveAndDate(boolean isActive, LocalDate holidayDate);

	List<Holiday> findAllByIsActiveTrue();

	List<Holiday> findAllByIsActiveTrueAndDateBetween(LocalDate startDate, LocalDate endDate);

	Holiday findByIsActiveTrueAndDate(LocalDate currentDate);

	List<Holiday> findAllByDate(LocalDate current);

}
