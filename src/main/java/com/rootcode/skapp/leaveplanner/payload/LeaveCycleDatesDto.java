package com.rootcode.skapp.leaveplanner.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class LeaveCycleDatesDto {

	LocalDate cycleStartDate;

	LocalDate cycleEndDate;

}
