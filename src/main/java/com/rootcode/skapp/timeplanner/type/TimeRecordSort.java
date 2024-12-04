package com.rootcode.skapp.timeplanner.type;

import lombok.Getter;

@Getter
public enum TimeRecordSort {

	NAME("name"), DATE("date");

	private final String sortField;

	TimeRecordSort(String sortField) {
		this.sortField = sortField;
	}

	@Override
	public String toString() {
		return this.sortField;
	}

}
