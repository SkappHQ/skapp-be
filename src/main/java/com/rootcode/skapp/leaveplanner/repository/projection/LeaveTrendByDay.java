package com.rootcode.skapp.leaveplanner.repository.projection;

import java.time.LocalDate;

public interface LeaveTrendByDay {

	LocalDate getLeaveDate();

	Integer getEmployeeCount();

}
