package com.skapp.community.peopleplanner.service;

import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.type.EmployeeTimelineType;

public interface EmployeeTimelineService {

	void addEmployeeTimelineRecord(Employee employee, EmployeeTimelineType timelineType, String title,
			String previousValue, String newValue);

	void addNewEmployeeTimeLineRecords(Employee employee, EmployeeDetailsDto employeeDetailsDto);

	ResponseEntityDto getEmployeeTimelineRecords(Long id);

}
