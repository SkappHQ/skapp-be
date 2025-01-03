package com.skapp.community.common.model;

import com.skapp.community.common.constant.AuthConstants;
import com.skapp.community.common.type.LoginMethod;
import com.skapp.community.common.type.Role;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "\"user\"")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false, updatable = false)
	private Long userId;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "temp_password")
	private String tempPassword;

	@Enumerated(EnumType.STRING)
	@Column(name = "login_method", columnDefinition = "varchar(255)")
	private LoginMethod loginMethod;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "is_password_changed")
	private Boolean isPasswordChangedForTheFirstTime = false;

	@Column(name = "previous_passwords")
	private String previousPasswords;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private Employee employee;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private UserSettings settings;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (employee.getEmployeeRole() == null) {
			return Collections.emptyList();
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		EmployeeRole employeeRole = employee.getEmployeeRole();

		if (Boolean.TRUE.equals(employeeRole.getIsSuperAdmin())) {
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + Role.SUPER_ADMIN));
		}

		Role peopleRole = employeeRole.getPeopleRole();
		if (peopleRole != null) {
			addRoleHierarchy(authorities, peopleRole, Role.PEOPLE_ADMIN, Role.PEOPLE_MANAGER, Role.PEOPLE_EMPLOYEE);
		}

		Role leaveRole = employeeRole.getLeaveRole();
		if (leaveRole != null) {
			addRoleHierarchy(authorities, leaveRole, Role.LEAVE_ADMIN, Role.LEAVE_MANAGER, Role.LEAVE_EMPLOYEE);
		}

		Role attendanceRole = employeeRole.getAttendanceRole();
		if (attendanceRole != null) {
			addRoleHierarchy(authorities, attendanceRole, Role.ATTENDANCE_ADMIN, Role.ATTENDANCE_MANAGER,
					Role.ATTENDANCE_EMPLOYEE);
		}

		return authorities;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}

	private void addRoleHierarchy(List<GrantedAuthority> authorities, Role currentRole, Role adminRole,
			Role managerRole, Role employeeRole) {
		if (currentRole == adminRole) {
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + adminRole));
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + managerRole));
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + employeeRole));
		}
		else if (currentRole == managerRole) {
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + managerRole));
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + employeeRole));
		}
		else if (currentRole == employeeRole) {
			authorities.add(new SimpleGrantedAuthority(AuthConstants.AUTH_ROLE + employeeRole));
		}
	}

	public List<String> getPreviousPasswordsList() {
		if (previousPasswords == null || previousPasswords.isEmpty()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(List.of(previousPasswords.split(",")));
	}

	public void addPreviousPassword(String password) {
		List<String> previousPasswordList = getPreviousPasswordsList();

		previousPasswordList.add(password);
		this.previousPasswords = String.join(",", previousPasswordList);
	}

}
