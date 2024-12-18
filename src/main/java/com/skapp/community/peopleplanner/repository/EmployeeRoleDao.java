package com.skapp.community.peopleplanner.repository;

import com.skapp.community.common.type.Role;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRoleDao extends JpaRepository<EmployeeRole, Long> {

	boolean existsByIsSuperAdminTrue();

	long countByIsSuperAdminTrue();

	List<EmployeeRole> findEmployeesByPeopleRole(Role roleName);

	long countByAttendanceRoleAndIsSuperAdmin(Role roleName, boolean isSuperAdmin);

	long countByPeopleRoleAndIsSuperAdmin(Role roleName, boolean isSuperAdmin);

	long countByLeaveRoleAndIsSuperAdmin(Role roleName, boolean isSuperAdmin);

}
