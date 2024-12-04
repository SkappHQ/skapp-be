package com.rootcode.skapp.leaveplanner.repository;

import com.rootcode.skapp.leaveplanner.model.LeaveRequest;
import com.rootcode.skapp.leaveplanner.model.LeaveRequestEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestEntitlementDao
		extends JpaRepository<LeaveRequestEntitlement, Long>, JpaSpecificationExecutor<LeaveRequestEntitlement> {

	List<LeaveRequestEntitlement> findAllByLeaveRequestOrderByLeaveEntitlement_ValidToAsc(LeaveRequest leaveRequest);

	List<LeaveRequestEntitlement> findAllByLeaveRequest(LeaveRequest leaveRequest);

}
