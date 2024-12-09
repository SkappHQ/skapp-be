package com.skapp.community.common.service;

import com.skapp.community.common.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

	UserDetailsService userDetailsService();

	User getCurrentUser();

}
