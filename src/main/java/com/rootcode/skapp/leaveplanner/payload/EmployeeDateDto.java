package com.rootcode.skapp.leaveplanner.payload;

import com.rootcode.skapp.peopleplanner.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class EmployeeDateDto {

	private Employee employee;

	private LocalDateTime creationDate;

}
