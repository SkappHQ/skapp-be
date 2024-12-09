package com.rootcode.skapp.common.service;

import com.rootcode.skapp.common.payload.request.ChangePasswordRequestDto;
import com.rootcode.skapp.common.payload.request.ForgotPasswordRequestDto;
import com.rootcode.skapp.common.payload.request.RefreshTokenRequestDto;
import com.rootcode.skapp.common.payload.request.ResetPasswordRequestDto;
import com.rootcode.skapp.common.payload.request.SignInRequestDto;
import com.rootcode.skapp.common.payload.request.SuperAdminSignUpRequestDto;
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
