package com.rootcode.skapp.leaveplanner.repository;

import com.rootcode.skapp.leaveplanner.model.CarryForwardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarryForwardInfoDao extends JpaRepository<CarryForwardInfo, Long>,
		JpaSpecificationExecutor<CarryForwardInfo>, CarryForwardInfoRepository {

}
