package com.rootcode.skapp.peopleplanner.type;

import lombok.Getter;

@Getter
public enum LeaveCycleConfigField {

	START("start"), END("end"), MONTH("month"), DATE("date"), IS_DEFAULT("isDefault");

	private final String field;

	LeaveCycleConfigField(String field) {
		this.field = field;
	}

}
