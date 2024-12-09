package com.skapp.community.common.constant;

import java.util.List;

public class FileConfigConstants {

	private FileConfigConstants() {
		throw new IllegalStateException("Illegal instantiate");
	}

	public static final String FILE_UPLOAD_PATH = "file.storage";

	public static final String FILE_PROFILE_PIC_PATH = "user-image";

	public static final String FILE_LEAVE_ATTACHMENTS_PATH = "leave-attachments";

	public static final String FILE_LOGO_PATH = "organization-logos";

	public static final String THUMBNAIL_SUFFIX = "-thumbnail";

	public static final int THUMBNAIL_HEIGHT = 150;

	public static final int THUMBNAIL_WIDTH = 150;

	public static final long FILE_UPLOAD_LIMIT_IN_PERCENTAGE = 10L;

	public final static List<String> SUPPORTED_FORMATS = List.of("jpg", "jpeg", "png", "gif", "bmp");

}
