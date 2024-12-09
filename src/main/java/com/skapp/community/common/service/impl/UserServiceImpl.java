package com.skapp.community.common.service.impl;

import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.repository.UserDao;
import com.skapp.community.common.service.UserService;
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
