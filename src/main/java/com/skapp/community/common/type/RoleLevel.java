package com.skapp.community.common.type;

import lombok.Getter;

@Getter
public enum RoleLevel {

	ADMIN("Admin"), MANAGER("Manager"), EMPLOYEE("Employee");

	private final String displayName;

	RoleLevel(String displayName) {
		this.displayName = displayName;
	}

}
