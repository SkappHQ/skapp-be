package com.rootcode.skapp.common.service.impl;

import com.rootcode.skapp.common.constant.CommonMessageConstant;
import com.rootcode.skapp.common.exception.ModuleException;
import com.rootcode.skapp.common.model.User;
import com.rootcode.skapp.common.repository.UserDao;
import com.rootcode.skapp.common.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	@NonNull
	private final UserDao userDao;

	@Override
	public UserDetailsService userDetailsService() {
		return username -> userDao.findByEmail(username)
			.orElseThrow(() -> new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND));
	}

	@Override
	public User getCurrentUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (User) userDetails;
	}

}
