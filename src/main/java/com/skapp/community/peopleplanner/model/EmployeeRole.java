package com.skapp.community.peopleplanner.model;

import com.skapp.community.common.type.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employee_role")
public class EmployeeRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_role_id", nullable = false, unique = true, updatable = false)
	private Long employeeRoleId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@Enumerated(EnumType.STRING)
	@Column(name = "people_role", length = 20, columnDefinition = "varchar(255)")
	private Role peopleRole;

	@Enumerated(EnumType.STRING)
	@Column(name = "leave_role", length = 20, columnDefinition = "varchar(255)")
	private Role leaveRole;

	@Enumerated(EnumType.STRING)
	@Column(name = "attendance_role", length = 20, columnDefinition = "varchar(255)")
	private Role attendanceRole;

	@Column(name = "is_super_admin", nullable = false)
	private Boolean isSuperAdmin = false;

	@Column(name = "permission_changed_date")
	private LocalDate changedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_changed_by", referencedColumnName = "employee_id")
	private Employee roleChangedBy;

}
