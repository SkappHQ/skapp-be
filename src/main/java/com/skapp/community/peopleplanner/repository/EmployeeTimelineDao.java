package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeTimeline;
import com.skapp.community.peopleplanner.type.EmployeeTimelineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTimelineDao
		extends JpaRepository<EmployeeTimeline, Long>, JpaSpecificationExecutor<EmployeeTimeline> {

	List<EmployeeTimeline> findAllByEmployee(Employee employee);

	List<EmployeeTimeline> findByEmployeeAndTimelineType(Employee employee, EmployeeTimelineType timelineType);

}
