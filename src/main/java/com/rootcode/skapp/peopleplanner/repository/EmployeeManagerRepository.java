package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.leaveplanner.type.ManagerType;
import com.rootcode.skapp.peopleplanner.model.Employee;

public interface EmployeeManagerRepository {

	void deleteByEmployeeAndManagerType(Employee employee, ManagerType managerType);

}
