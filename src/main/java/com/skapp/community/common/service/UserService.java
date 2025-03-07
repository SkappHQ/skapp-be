package com.skapp.community.common.service;

import com.skapp.community.common.model.User;
import com.skapp.enterprise.common.type.Tier;

public interface UserService {

	User getCurrentUser();

	Tier getCurrentUserTier();

}
