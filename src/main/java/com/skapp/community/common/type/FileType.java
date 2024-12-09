package com.skapp.community.common.type;

public enum FileType {

	ORGANIZATION_LOGOS("organization-logos"), USER_IMAGE("user-image"), LEAVE_ATTACHMENTS("leave-attachments");

	public final String label;

	private FileType(String label) {
		this.label = label;
	}

}
