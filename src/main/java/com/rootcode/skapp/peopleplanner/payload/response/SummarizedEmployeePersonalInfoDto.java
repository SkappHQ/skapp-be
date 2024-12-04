package com.rootcode.skapp.peopleplanner.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SummarizedEmployeePersonalInfoDto {

	private LocalDate birthDate;

	private String nationality;

}
