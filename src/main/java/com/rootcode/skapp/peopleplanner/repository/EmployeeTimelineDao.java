package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.EmployeeTimeline;
import com.rootcode.skapp.peopleplanner.type.EmployeeTimelineType;
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
