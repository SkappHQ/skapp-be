package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeDetailsDto;
import com.rootcode.skapp.peopleplanner.type.EmployeeTimelineType;

public interface EmployeeTimelineService {

	void addEmployeeTimelineRecord(Employee employee, EmployeeTimelineType timelineType, String title,
			String previousValue, String newValue);

	void addNewEmployeeTimeLineRecords(Employee employee, EmployeeDetailsDto employeeDetailsDto);

	ResponseEntityDto getEmployeeTimelineRecords(Long id);

}
