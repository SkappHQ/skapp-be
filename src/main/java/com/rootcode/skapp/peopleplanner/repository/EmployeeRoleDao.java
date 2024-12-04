package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.common.type.Role;
import com.rootcode.skapp.peopleplanner.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRoleDao extends JpaRepository<EmployeeRole, Long> {

	boolean existsByIsSuperAdminTrue();

	long countByIsSuperAdminTrue();

	List<EmployeeRole> findEmployeesByPeopleRole(Role roleName);

}
