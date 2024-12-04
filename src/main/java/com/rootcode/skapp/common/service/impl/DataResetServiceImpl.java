package com.rootcode.skapp.common.service.impl;

import com.rootcode.skapp.common.constant.CommonMessageConstant;
import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.common.repository.DataResetDao;
import com.rootcode.skapp.common.service.DataResetService;
import com.rootcode.skapp.common.util.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RequiredArgsConstructor
@Service
public class DataResetServiceImpl implements DataResetService {

	@NonNull
	private final DataResetDao dataResetDao;

	@NonNull
	private final MessageUtil messageUtil;

	@Override
	@Transactional
	public ResponseEntityDto resetDatabase() {
		log.info("resetDatabase: execution started");
		dataResetDao.resetDatabase();
		log.info("resetDatabase: execution ended");
		return new ResponseEntityDto(messageUtil.getMessage(CommonMessageConstant.COMMON_DATABASE_RESET_SUCCESS),
				false);
	}

}
