package com.rootcode.skapp.timeplanner.repository;

import com.rootcode.skapp.timeplanner.model.AttendanceConfig;
import com.rootcode.skapp.timeplanner.type.AttendanceConfigType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendanceConfigDao extends JpaRepository<AttendanceConfig, AttendanceConfigType>,
		JpaSpecificationExecutor<AttendanceConfig>, AttendanceConfigRepository {

	AttendanceConfig findByAttendanceConfigType(AttendanceConfigType attendanceConfigType);

}
