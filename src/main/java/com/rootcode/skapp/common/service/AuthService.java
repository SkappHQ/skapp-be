package com.rootcode.skapp.common.service;

import com.rootcode.skapp.common.payload.request.*;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;

public interface AuthService {

	ResponseEntityDto signIn(SignInRequestDto signInRequestDto);

	ResponseEntityDto superAdminSignUp(SuperAdminSignUpRequestDto superAdminSignUpRequestDto);

	ResponseEntityDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequestDto);

	ResponseEntityDto employeeResetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

	ResponseEntityDto sharePassword(Long userId);

	ResponseEntityDto resetAndSharePassword(Long userId);

	ResponseEntityDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);

	ResponseEntityDto changePassword(ChangePasswordRequestDto changePasswordRequestDto, Long userId);

}
