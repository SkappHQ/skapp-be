package com.rootcode.skapp.timeplanner.payload.projection;

import java.time.LocalDate;

public interface EmployeeWorkHours {

	LocalDate getDate();

	Double getWorkedHours();

}
