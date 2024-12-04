package com.rootcode.skapp.leaveplanner.service;

import com.rootcode.skapp.leaveplanner.payload.LeaveCycleDetailsDto;

import java.time.LocalDate;

public interface LeaveCycleService {

	LeaveCycleDetailsDto getLeaveCycleConfigs();

	LocalDate getLeaveCycleStartDate();

	LocalDate getLeaveCycleEndDate();

	boolean isInNextCycle(int startYear);

	boolean isInCurrentCycle(int year);

	boolean isInPreviousCycle(int year);

	void setLeaveCycleDefaultConfigs();

}
