package com.rootcode.skapp.leaveplanner.repository;

import com.rootcode.skapp.leaveplanner.model.CarryForwardInfo;

import java.time.LocalDate;
import java.util.Optional;

public interface CarryForwardInfoRepository {

	Optional<CarryForwardInfo> findByEmployeeEmployeeIdAndLeaveTypeTypeIdAndCycleEndDate(Long employeeId, Long typeId,
			LocalDate leaveCycleEndDate);

}
