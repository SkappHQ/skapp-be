package com.skapp.community.common.constant;

public class AuthConstants {

	private AuthConstants() {
		throw new IllegalStateException("Illegal instantiate");
	}

	public static final String AUTH_ROLE = "ROLE_";

	public static final String TOKEN_TYPE = "token_type";

	public static final String ROLES = "roles";

	public static final String AUTHORIZATION = "Authorization";

	public static final String BEARER = "Bearer ";

	public static final String TOKEN = "token";

	public static final String USER_ID = "userId";

	public static final String RESET_DATABASE_API_HEADER = "X-Reset-Database-Key";

}
