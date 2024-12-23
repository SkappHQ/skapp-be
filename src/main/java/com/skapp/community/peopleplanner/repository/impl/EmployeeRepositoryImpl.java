package com.skapp.community.peopleplanner.repository.impl;

import com.skapp.community.common.model.Auditable_;
import com.skapp.community.common.model.User;
import com.skapp.community.common.model.User_;
import com.skapp.community.common.type.Role;
import com.skapp.community.leaveplanner.model.LeaveRequest;
import com.skapp.community.leaveplanner.model.LeaveRequest_;
import com.skapp.community.leaveplanner.payload.AdminOnLeaveDto;
import com.skapp.community.leaveplanner.payload.EmployeeLeaveRequestDto;
import com.skapp.community.leaveplanner.payload.EmployeesOnLeaveFilterDto;
import com.skapp.community.leaveplanner.type.LeaveRequestStatus;
import com.skapp.community.leaveplanner.type.ManagerType;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import com.skapp.community.peopleplanner.model.EmployeeManager_;
import com.skapp.community.peopleplanner.model.EmployeePersonalInfo;
import com.skapp.community.peopleplanner.model.EmployeePersonalInfo_;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.model.EmployeeRole_;
import com.skapp.community.peopleplanner.model.EmployeeTeam;
import com.skapp.community.peopleplanner.model.EmployeeTeam_;
import com.skapp.community.peopleplanner.model.Employee_;
import com.skapp.community.peopleplanner.model.JobFamily_;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.peopleplanner.model.Team_;
import com.skapp.community.peopleplanner.payload.request.EmployeeFilterDto;
import com.skapp.community.peopleplanner.payload.request.PermissionFilterDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeCountDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeManagerDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeTeamDto;
import com.skapp.community.peopleplanner.repository.EmployeeRepository;
import com.skapp.community.peopleplanner.type.AccountStatus;
import com.skapp.community.peopleplanner.type.EmployeeType;
import com.skapp.community.peopleplanner.type.EmploymentAllocation;
import com.skapp.community.peopleplanner.type.Gender;
import com.skapp.community.timeplanner.model.TimeRecord;
import com.skapp.community.timeplanner.model.TimeRecord_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.skapp.community.peopleplanner.util.PeopleUtil.getSearchString;

@Component
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

	@NonNull
	private final EntityManager entityManager;

	@Override
	public Optional<Employee> findEmployeeByEmployeeIdAndUserActiveNot(Long employeeId, boolean activeState) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();
		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.equal(root.get(Employee_.employeeId), employeeId));
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), activeState));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);

		TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Page<Employee> findEmployees(EmployeeFilterDto employeeFilterDto, Pageable page) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		Join<Employee, User> userJoin = root.join(Employee_.user);
		Join<Employee, EmployeePersonalInfo> personalInfoJoin = root.join((Employee_.personalInfo), JoinType.LEFT);
		Join<Employee, EmployeeRole> roleJoin = root.join((Employee_.employeeRole));

		List<Predicate> predicates = new ArrayList<>();

		if (employeeFilterDto.getRole() != null && !employeeFilterDto.getRole().isEmpty()) {
			predicates
				.add(root.get(Employee_.JOB_FAMILY).get(JobFamily_.JOB_FAMILY_ID).in(employeeFilterDto.getRole()));
		}

		if (employeeFilterDto.getTeam() != null && !employeeFilterDto.getTeam().isEmpty()) {
			Join<Employee, EmployeeTeam> employeeTeam = root.join(Employee_.teams);
			predicates.add(employeeTeam.get(EmployeeTeam_.TEAM).get(Team_.TEAM_ID).in(employeeFilterDto.getTeam()));
		}

		if (employeeFilterDto.getNationality() != null && !employeeFilterDto.getNationality().isEmpty()) {
			predicates
				.add(personalInfoJoin.get(EmployeePersonalInfo_.NATIONALITY).in(employeeFilterDto.getNationality()));
		}

		if (employeeFilterDto.getGender() != null) {
			predicates.add(criteriaBuilder.equal(root.get(Employee_.GENDER), employeeFilterDto.getGender()));
		}

		if (employeeFilterDto.getSearchKeyword().isEmpty()) {
			addEmploymentStatusPredicate(employeeFilterDto, criteriaBuilder, root, userJoin, predicates);
		}

		if (employeeFilterDto.getEmploymentTypes() != null && !employeeFilterDto.getEmploymentTypes().isEmpty()) {
			predicates.add(root.get(Employee_.EMPLOYEE_TYPE).in(employeeFilterDto.getEmploymentTypes()));
		}

		if (employeeFilterDto.getEmploymentAllocations() != null
				&& !employeeFilterDto.getEmploymentAllocations().isEmpty()) {
			predicates.add(root.get(Employee_.EMPLOYMENT_ALLOCATION).in(employeeFilterDto.getEmploymentAllocations()));
		}

		if (employeeFilterDto.getSearchKeyword() != null && !employeeFilterDto.getSearchKeyword().isEmpty()) {
			predicates.add(findByEmailName(employeeFilterDto.getSearchKeyword(), criteriaBuilder, root, userJoin));
		}

		if (employeeFilterDto.getPermissions() != null && !employeeFilterDto.getPermissions().isEmpty()) {
			Predicate attendanceRolePredicate = roleJoin.get(EmployeeRole_.ATTENDANCE_ROLE)
				.in(employeeFilterDto.getPermissions());
			Predicate peopleRolePredicate = roleJoin.get(EmployeeRole_.PEOPLE_ROLE)
				.in(employeeFilterDto.getPermissions());
			Predicate leaveRolePredicate = roleJoin.get(EmployeeRole_.LEAVE_ROLE)
				.in(employeeFilterDto.getPermissions());

			Predicate rolePredicate = criteriaBuilder.or(attendanceRolePredicate, peopleRolePredicate,
					leaveRolePredicate);
			predicates.add(rolePredicate);
		}

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);

		if (employeeFilterDto.getSearchKeyword() != null && !employeeFilterDto.getSearchKeyword().isEmpty()) {
			List<Order> orderList = new ArrayList<>();
			Order sortingOrder = criteriaBuilder.asc(criteriaBuilder.selectCase()
				.when(criteriaBuilder.like(root.get(Employee_.FIRST_NAME),
						getSearchString(employeeFilterDto.getSearchKeyword())), 1)
				.when(criteriaBuilder.like(root.get(Employee_.LAST_NAME),
						getSearchString(employeeFilterDto.getSearchKeyword())), 2)
				.when(criteriaBuilder.like(userJoin.get(User_.EMAIL),
						getSearchString(employeeFilterDto.getSearchKeyword())), 3)
				.otherwise(4));
			orderList.add(sortingOrder);
			orderList.addAll(QueryUtils.toOrders(page.getSort(), root, criteriaBuilder));
			criteriaQuery.orderBy(orderList);
		}
		else {
			criteriaQuery.distinct(true);
			criteriaQuery.orderBy(QueryUtils.toOrders(page.getSort(), root, criteriaBuilder));
		}

		TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);

		int totalRows = query.getResultList().size();
		query.setFirstResult(page.getPageNumber() * page.getPageSize());
		query.setMaxResults(page.getPageSize());

		return new PageImpl<>(query.getResultList(), page, totalRows);
	}

	@Override
	public List<EmployeeTeamDto> findTeamsByEmployees(List<Long> employeeIDs) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<EmployeeTeamDto> criteriaQuery = criteriaBuilder.createQuery(EmployeeTeamDto.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, EmployeeTeam> employeeTeam = root.join(Employee_.TEAMS);
		Join<EmployeeTeam, Team> team = employeeTeam.join(EmployeeTeam_.TEAM);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(root.get(Employee_.EMPLOYEE_ID).in(employeeIDs));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.multiselect(root.get(Employee_.EMPLOYEE_ID), team);
		TypedQuery<EmployeeTeamDto> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<EmployeeManagerDto> findManagersByEmployeeIds(List<Long> employeeId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<EmployeeManagerDto> criteriaQuery = criteriaBuilder.createQuery(EmployeeManagerDto.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, EmployeeManager> managers = root.join(Employee_.EMPLOYEES);
		Join<EmployeeManager, Employee> manEmp = managers.join(EmployeeManager_.MANAGER);
		Join<Employee, User> userJoin = root.join(Employee_.user);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));
		predicates.add((root.get(Employee_.employeeId).in(employeeId)));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.multiselect(root.get(Employee_.employeeId), manEmp);
		TypedQuery<EmployeeManagerDto> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public EmployeeCountDto getLoginPendingEmployeeCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, User> userJoin = root.join(Employee_.user);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(Employee_.ACCOUNT_STATUS), AccountStatus.PENDING));
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

		return new EmployeeCountDto(query.getSingleResult());
	}

	@Override
	public List<Employee> findEmployeeByNameEmail(String keyword, PermissionFilterDto permissionFilterDto) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);
		Join<Employee, EmployeeRole> roleJoin = root.join(Employee_.EMPLOYEE_ROLE);

		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));

		if (keyword != null && !keyword.trim().isEmpty()) {
			predicates.add(findByEmailName(keyword, criteriaBuilder, root, userJoin));
		}

		if (permissionFilterDto != null && permissionFilterDto.getUserRole() != null) {
			int minAttendancePrivilege = 0;
			int minLeavePrivilege = 0;
			int minPeoplePrivilege = 0;

			switch (permissionFilterDto.getUserRole()) {
				case EMPLOYEES -> {
					minAttendancePrivilege = 1;
					minLeavePrivilege = 1;
					minPeoplePrivilege = 1;
				}
				case MANAGERS -> {
					minAttendancePrivilege = 2;
					minLeavePrivilege = 2;
					minPeoplePrivilege = 2;
				}
				case SUPER_ADMIN -> {
					minAttendancePrivilege = 3;
					minLeavePrivilege = 3;
					minPeoplePrivilege = 3;
				}
			}

			predicates.add(criteriaBuilder
				.greaterThanOrEqualTo(criteriaBuilder.selectCase(roleJoin.get(EmployeeRole_.attendanceRole))
					.when(Role.ATTENDANCE_ADMIN, 3)
					.when(Role.ATTENDANCE_MANAGER, 2)
					.when(Role.ATTENDANCE_EMPLOYEE, 1)
					.otherwise(0)
					.as(Integer.class), criteriaBuilder.literal(minAttendancePrivilege)));

			predicates.add(criteriaBuilder
				.greaterThanOrEqualTo(criteriaBuilder.selectCase(roleJoin.get(EmployeeRole_.leaveRole))
					.when(Role.LEAVE_ADMIN, 3)
					.when(Role.LEAVE_MANAGER, 2)
					.when(Role.LEAVE_EMPLOYEE, 1)
					.otherwise(0)
					.as(Integer.class), criteriaBuilder.literal(minLeavePrivilege)));

			predicates.add(criteriaBuilder
				.greaterThanOrEqualTo(criteriaBuilder.selectCase(roleJoin.get(EmployeeRole_.peopleRole))
					.when(Role.PEOPLE_ADMIN, 3)
					.when(Role.PEOPLE_MANAGER, 2)
					.when(Role.PEOPLE_EMPLOYEE, 1)
					.otherwise(0)
					.as(Integer.class), criteriaBuilder.literal(minPeoplePrivilege)));
		}

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);

		List<Order> orderList = new ArrayList<>();
		if (keyword != null && !keyword.trim().isEmpty()) {
			Order sortingOrder = criteriaBuilder.asc(criteriaBuilder.selectCase()
				.when(criteriaBuilder.like(root.get("firstName"), getSearchString(keyword)), 1)
				.when(criteriaBuilder.like(root.get("lastName"), getSearchString(keyword)), 2)
				.when(criteriaBuilder.like(userJoin.get("email"), getSearchString(keyword)), 3)
				.otherwise(4));
			orderList.add(sortingOrder);
		}
		else {
			orderList.add(criteriaBuilder.asc(root.get(Employee_.FIRST_NAME)));
		}
		criteriaQuery.orderBy(orderList);

		TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public Employee findEmployeeByEmail(String email) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		Join<Employee, User> userJoin = root.join(Employee_.user);

		Predicate emailPredicate = criteriaBuilder.equal(userJoin.get(User_.email), email);
		criteriaQuery.where(emailPredicate);

		TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);

		List<Employee> results = typedQuery.getResultList();
		return results.isEmpty() ? null : results.getFirst();
	}

	@Override
	public Page<Employee> findEmployeesByManagerId(Long managerId, Pageable page) {
		return findEmployeesByManagerId(managerId, page, false);
	}

	@Override
	public AdminOnLeaveDto findAllEmployeesOnLeave(EmployeesOnLeaveFilterDto filterDto, Long currentUserId,
			boolean isLeaveAdmin) {
		if (filterDto.getTeamIds() == null || filterDto.getTeamIds().isEmpty()) {
			return new AdminOnLeaveDto(0L, 0L);
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminOnLeaveDto> criteriaQuery = criteriaBuilder.createQuery(AdminOnLeaveDto.class);
		Root<LeaveRequest> root = criteriaQuery.from(LeaveRequest.class);

		Join<LeaveRequest, Employee> employee = root.join(LeaveRequest_.employee);
		List<Predicate> predicates = new ArrayList<>();

		Subquery<Long> totalEmployeesSubquery = criteriaQuery.subquery(Long.class);
		Root<Employee> totalEmployeeRoot = totalEmployeesSubquery.from(Employee.class);
		Join<Employee, User> userJoin = totalEmployeeRoot.join(Employee_.user);

		if (filterDto.getTeamIds().contains(-1L)) {
			if (isLeaveAdmin) {
				totalEmployeesSubquery.select(criteriaBuilder.countDistinct(totalEmployeeRoot))
					.where(criteriaBuilder.equal(userJoin.get(User_.isActive), true));
			}
			else {
				Subquery<Long> managedEmployeesSubquery = criteriaQuery.subquery(Long.class);
				Root<EmployeeManager> managerRoot = managedEmployeesSubquery.from(EmployeeManager.class);
				managedEmployeesSubquery.select(managerRoot.get(EmployeeManager_.employee).get(Employee_.employeeId))
					.where(criteriaBuilder.equal(managerRoot.get(EmployeeManager_.manager).get(Employee_.employeeId),
							currentUserId));

				Subquery<Long> supervisedTeamsSubquery = criteriaQuery.subquery(Long.class);
				Root<EmployeeTeam> supervisorTeamRoot = supervisedTeamsSubquery.from(EmployeeTeam.class);
				supervisedTeamsSubquery.select(supervisorTeamRoot.get(EmployeeTeam_.team).get(Team_.teamId))
					.where(criteriaBuilder.and(criteriaBuilder
						.equal(supervisorTeamRoot.get(EmployeeTeam_.employee).get(Employee_.employeeId), currentUserId),
							criteriaBuilder.isTrue(supervisorTeamRoot.get(EmployeeTeam_.isSupervisor))));

				Subquery<Long> teamMembersSubquery = criteriaQuery.subquery(Long.class);
				Root<EmployeeTeam> teamRoot = teamMembersSubquery.from(EmployeeTeam.class);
				teamMembersSubquery.select(teamRoot.get(EmployeeTeam_.employee).get(Employee_.employeeId))
					.where(teamRoot.get(EmployeeTeam_.team).get(Team_.teamId).in(supervisedTeamsSubquery));

				Subquery<Long> employeesSubquery = criteriaQuery.subquery(Long.class);
				Root<Employee> employeeRoot = employeesSubquery.from(Employee.class);
				employeesSubquery.select(employeeRoot.get(Employee_.employeeId))
					.where(criteriaBuilder.or(employeeRoot.get(Employee_.employeeId).in(managedEmployeesSubquery),
							employeeRoot.get(Employee_.employeeId).in(teamMembersSubquery)));

				predicates.add(employee.get(Employee_.employeeId).in(employeesSubquery));

				totalEmployeesSubquery.select(criteriaBuilder.countDistinct(totalEmployeeRoot))
					.where(criteriaBuilder.and(criteriaBuilder.equal(userJoin.get(User_.isActive), true),
							totalEmployeeRoot.get(Employee_.employeeId).in(employeesSubquery)));
			}
		}
		else {
			Join<Employee, EmployeeTeam> employeeTeamJoin = employee.join(Employee_.teams);
			Join<Employee, EmployeeTeam> totalEmployeeTeamJoin = totalEmployeeRoot.join(Employee_.teams);

			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(filterDto.getTeamIds()));

			totalEmployeesSubquery.select(criteriaBuilder.countDistinct(totalEmployeeRoot))
				.where(criteriaBuilder.and(
						totalEmployeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(filterDto.getTeamIds()),
						criteriaBuilder.equal(userJoin.get(User_.isActive), true)));
		}

		if (filterDto.getDate() != null) {
			predicates.add(criteriaBuilder.and(
					criteriaBuilder.lessThanOrEqualTo(root.get(LeaveRequest_.startDate), filterDto.getDate()),
					criteriaBuilder.greaterThanOrEqualTo(root.get(LeaveRequest_.endDate), filterDto.getDate())));
		}

		predicates.add(root.get(LeaveRequest_.STATUS).in(LeaveRequestStatus.APPROVED, LeaveRequestStatus.PENDING));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.multiselect(criteriaBuilder.countDistinct(employee), totalEmployeesSubquery);

		return entityManager.createQuery(criteriaQuery).getSingleResult();
	}

	@Override
	public List<Long> findEmployeeIdsByManagerId(Long managerId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, EmployeeManager> empJoin = root.join(Employee_.employees);
		Join<EmployeeManager, Employee> empMan = empJoin.join(EmployeeManager_.manager);
		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));
		predicates.add(criteriaBuilder.equal(empMan.get(Employee_.employeeId), managerId));
		predicates.add(criteriaBuilder.notEqual(empJoin.get(EmployeeManager_.MANAGER_TYPE), ManagerType.INFORMANT));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.select(root.get(Employee_.employeeId));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

		return query.getResultList();
	}

	@Override
	public Long findAllActiveEmployeesCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), true));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

		return query.getSingleResult();
	}

	@Override
	public List<Employee> findManagersByEmployeeIdAndLoggedInManagerId(@lombok.NonNull Long employeeId,
			Long managerId) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, EmployeeManager> managers = root.join(Employee_.managers);
		Join<EmployeeManager, Employee> manEmp = managers.join(EmployeeManager_.employee);
		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.equal(manEmp.get(Employee_.employeeId), employeeId));

		predicates.add(criteriaBuilder.equal(managers.get(EmployeeManager_.MANAGER), managerId));
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);

		TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);

		return query.getResultList();
	}

	@Override
	public List<EmployeeLeaveRequestDto> getEmployeesOnLeaveByTeam(EmployeesOnLeaveFilterDto filterDto,
			Long currentUserId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveRequestDto> criteriaQuery = criteriaBuilder
			.createQuery(EmployeeLeaveRequestDto.class);
		Root<LeaveRequest> root = criteriaQuery.from(LeaveRequest.class);

		List<Predicate> predicates = new ArrayList<>();

		// Role-based subquery
		Subquery<String> leaveRoleSubquery = criteriaQuery.subquery(String.class);
		Root<EmployeeRole> employeeRoleRoot = leaveRoleSubquery.from(EmployeeRole.class);
		leaveRoleSubquery.select(employeeRoleRoot.get(EmployeeRole_.leaveRole).as(String.class))
			.where(criteriaBuilder.equal(employeeRoleRoot.get(EmployeeRole_.employee).get(Employee_.employeeId),
					currentUserId));

		Predicate isAdminPredicate = criteriaBuilder.equal(leaveRoleSubquery,
				criteriaBuilder.literal(Role.LEAVE_ADMIN.name()));

		// Initialize employeeJoin with a default value
		Join<LeaveRequest, Employee> employeeJoin = root.join(LeaveRequest_.employee);

		if (filterDto.getTeamIds() != null && !filterDto.getTeamIds().isEmpty()
				&& filterDto.getTeamIds().contains(-1L)) {
			// Handle `LEAVE_MANAGER` logic: Managed and supervised employees
			Subquery<Long> managedEmployeesSubquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeManager> managerRoot = managedEmployeesSubquery.from(EmployeeManager.class);
			managedEmployeesSubquery.select(managerRoot.get(EmployeeManager_.employee).get(Employee_.employeeId))
				.where(criteriaBuilder.equal(managerRoot.get(EmployeeManager_.manager).get(Employee_.employeeId),
						currentUserId));

			Subquery<Long> supervisedTeamsSubquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeTeam> supervisorTeamRoot = supervisedTeamsSubquery.from(EmployeeTeam.class);
			supervisedTeamsSubquery.select(supervisorTeamRoot.get(EmployeeTeam_.team).get(Team_.teamId))
				.where(criteriaBuilder.equal(supervisorTeamRoot.get(EmployeeTeam_.employee).get(Employee_.employeeId),
						currentUserId));

			Subquery<Long> teamMembersSubquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeTeam> teamRoot = teamMembersSubquery.from(EmployeeTeam.class);
			teamMembersSubquery.select(teamRoot.get(EmployeeTeam_.employee).get(Employee_.employeeId))
				.where(teamRoot.get(EmployeeTeam_.team).get(Team_.teamId).in(supervisedTeamsSubquery));

			predicates.add(criteriaBuilder.or(isAdminPredicate,
					employeeJoin.get(Employee_.employeeId).in(managedEmployeesSubquery),
					employeeJoin.get(Employee_.employeeId).in(teamMembersSubquery)));
		}
		else if (filterDto.getTeamIds() != null && !filterDto.getTeamIds().isEmpty()) {
			// Filter by specific team IDs
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(LeaveRequest_.employee).join(Employee_.teams);
			predicates.add(criteriaBuilder.and(criteriaBuilder.not(isAdminPredicate),
					employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(filterDto.getTeamIds())));
		}
		else {
			// Default to role-based filtering
			predicates.add(isAdminPredicate);
		}

		// Apply date filter
		if (filterDto.getDate() != null) {
			predicates.add(criteriaBuilder.equal(root.get(LeaveRequest_.startDate), filterDto.getDate()));
		}

		// Apply status filter
		predicates.add(root.get(LeaveRequest_.STATUS).in(LeaveRequestStatus.APPROVED, LeaveRequestStatus.PENDING));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.multiselect(employeeJoin, root).distinct(true);

		TypedQuery<EmployeeLeaveRequestDto> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<Employee> findInformantManagersByEmployeeID(Long employeeId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, EmployeeManager> managers = root.join(Employee_.managers);
		Join<EmployeeManager, Employee> manEmp = managers.join(EmployeeManager_.employee);
		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));
		predicates.add(criteriaBuilder.equal(manEmp.get(Employee_.employeeId), employeeId));
		predicates.add(criteriaBuilder.equal(managers.get(EmployeeManager_.MANAGER_TYPE), ManagerType.INFORMANT));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);

		TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	@Override
	public Long countByIsActiveAndTeams(List<Long> teamIds, AccountStatus accountStatus) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0L;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), true));
		predicates.add(criteriaBuilder.equal(root.get(Employee_.accountStatus), accountStatus));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public Long countByIsActiveAndTeamsAndCreatedAt(boolean isActive, List<Long> teamIds, int currentYear) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0L;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Expression<Integer> joinYear = criteriaBuilder.function("YEAR", Integer.class, root.get(Employee_.JOIN_DATE));
		Expression<Integer> terminationYear = criteriaBuilder.function("YEAR", Integer.class,
				root.get(Employee_.TERMINATION_DATE));

		if (isActive) {
			predicates.add(criteriaBuilder.equal(joinYear, currentYear));
			predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(root.get(Employee_.TERMINATION_DATE)),
					criteriaBuilder.notEqual(terminationYear, currentYear)));
		}
		else {
			predicates.add(criteriaBuilder.and(criteriaBuilder.isNotNull(root.get(Employee_.TERMINATION_DATE)),
					criteriaBuilder.equal(terminationYear, currentYear)));
		}

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public Long findTotalAgeOfActiveEmployeesByTeamIds(Long teamId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);
		Join<Employee, EmployeePersonalInfo> personalInfoJoin = root.join(Employee_.personalInfo);

		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), true));

		if (teamId != null) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(criteriaBuilder.equal(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId), teamId));
		}

		Expression<Integer> birthYear = criteriaBuilder.function("YEAR", Integer.class,
				personalInfoJoin.get(EmployeePersonalInfo_.birthDate));
		Expression<Integer> currentYear = criteriaBuilder.function("YEAR", Integer.class,
				criteriaBuilder.currentDate());
		Expression<Integer> age = criteriaBuilder.diff(currentYear, birthYear);

		criteriaQuery.select(criteriaBuilder.coalesce(criteriaBuilder.sum(criteriaBuilder.toLong(age)), 0L));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public Double findAverageAgeOfActiveEmployeesByTeamIds(List<Long> teamIds) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0.0;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> sumQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> sumRoot = sumQuery.from(Employee.class);
		List<Predicate> sumPredicates = new ArrayList<>();

		Join<Employee, User> sumUserJoin = sumRoot.join(Employee_.user);
		Join<Employee, EmployeePersonalInfo> sumPersonalInfoJoin = sumRoot.join(Employee_.personalInfo);

		sumPredicates.add(criteriaBuilder.equal(sumUserJoin.get(User_.isActive), true));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> sumEmployeeTeamJoin = sumRoot.join(Employee_.teams);
			sumPredicates.add(sumEmployeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		Predicate sumBirthDateIsNotNull = criteriaBuilder
			.isNotNull(sumPersonalInfoJoin.get(EmployeePersonalInfo_.birthDate));
		sumPredicates.add(sumBirthDateIsNotNull);

		Expression<Integer> sumBirthYear = criteriaBuilder.function("YEAR", Integer.class,
				sumPersonalInfoJoin.get(EmployeePersonalInfo_.birthDate));
		Expression<Integer> sumCurrentYear = criteriaBuilder.function("YEAR", Integer.class,
				criteriaBuilder.currentDate());
		Expression<Integer> sumAge = criteriaBuilder.diff(sumCurrentYear, sumBirthYear);

		sumQuery.select(criteriaBuilder.coalesce(criteriaBuilder.sum(criteriaBuilder.toLong(sumAge)), 0L));
		sumQuery.where(sumPredicates.toArray(new Predicate[0]));

		TypedQuery<Long> sumQueryResult = entityManager.createQuery(sumQuery);
		Long totalAge = sumQueryResult.getSingleResult();

		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> countRoot = countQuery.from(Employee.class);
		List<Predicate> countPredicates = new ArrayList<>();

		Join<Employee, User> countUserJoin = countRoot.join(Employee_.user);
		Join<Employee, EmployeePersonalInfo> countPersonalInfoJoin = countRoot.join(Employee_.personalInfo);

		countPredicates.add(criteriaBuilder.equal(countUserJoin.get(User_.isActive), true));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> countEmployeeTeamJoin = countRoot.join(Employee_.teams);
			countPredicates.add(countEmployeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		Predicate countBirthDateIsNotNull = criteriaBuilder
			.isNotNull(countPersonalInfoJoin.get(EmployeePersonalInfo_.birthDate));
		countPredicates.add(countBirthDateIsNotNull);

		countQuery.select(criteriaBuilder.count(countRoot));
		countQuery.where(countPredicates.toArray(new Predicate[0]));

		TypedQuery<Long> countQueryResult = entityManager.createQuery(countQuery);
		Long numberOfEmployeesWithBirthDates = countQueryResult.getSingleResult();

		if (numberOfEmployeesWithBirthDates == 0) {
			return 0.0;
		}
		else {
			return totalAge.doubleValue() / numberOfEmployeesWithBirthDates;
		}
	}

	@Override
	public Long countTerminatedEmployeesByStartDateAndEndDateAndTeams(LocalDate startDate, LocalDate endDate,
			List<Long> teamIds) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0L;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);

		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), false));

		Expression<LocalDate> terminationDate = root.get(Employee_.terminationDate);
		predicates.add(criteriaBuilder.isNotNull(terminationDate));
		predicates.add(criteriaBuilder.between(terminationDate, startDate, endDate));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public Long countByCreateDateRangeAndTeams(LocalDate endDate, List<Long> teamIds) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0L;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Expression<LocalDate> createdDateAsLocalDate = criteriaBuilder.function("DATE", LocalDate.class,
				root.get(Auditable_.createdDate));
		predicates.add(criteriaBuilder.lessThanOrEqualTo(createdDateAsLocalDate, endDate));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(new ArrayList<>(teamIds)));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public Long countByIsActiveAndTeamsAndGender(boolean isActive, List<Long> teamIds, Gender gender) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0L;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);

		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), true));
		predicates.add(criteriaBuilder.equal(root.get(Employee_.GENDER), gender));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public Long countByEmploymentTypeAndEmploymentAllocationAndTeams(EmployeeType employmentType,
			EmploymentAllocation employmentAllocation, List<Long> teamIds) {
		if (teamIds != null && teamIds.isEmpty()) {
			return 0L;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), true));

		if (employmentType != null) {
			predicates.add(criteriaBuilder.equal(root.get(Employee_.EMPLOYEE_TYPE), employmentType));
		}

		if (employmentAllocation != null) {
			predicates.add(criteriaBuilder.equal(root.get(Employee_.EMPLOYMENT_ALLOCATION), employmentAllocation));
		}

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.distinct(true);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public List<Employee> findByNameAndIsActiveAndTeam(String employeeName, Boolean isActive, Long teamId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);

		predicates.add(criteriaBuilder.equal(userJoin.get(User_.isActive), isActive));

		if (employeeName != null) {
			employeeName = getSearchString(employeeName);
			String lowerEmployeeName = employeeName.toLowerCase();

			predicates.add(criteriaBuilder.or(
					criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.concat(
							criteriaBuilder.concat(root.get(Employee_.firstName), " "), root.get(Employee_.lastName))),
							lowerEmployeeName),
					criteriaBuilder.like(criteriaBuilder.lower(root.get(Employee_.lastName)), lowerEmployeeName)));
		}

		if (teamId != null) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = root.join(Employee_.teams);
			predicates.add(criteriaBuilder.equal(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId), teamId));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		List<Order> orderList = new ArrayList<>();
		orderList.add(criteriaBuilder.asc(criteriaBuilder.selectCase()
			.when(criteriaBuilder.like(root.get(Employee_.firstName), employeeName), 1)
			.when(criteriaBuilder.like(root.get(Employee_.lastName), employeeName), 2)
			.otherwise(3)));

		orderList.add(criteriaBuilder.asc(root.get(Employee_.firstName)));
		orderList.add(criteriaBuilder.asc(root.get(Employee_.lastName)));
		criteriaQuery.orderBy(orderList);

		TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public Long countEmployeesByManagerId(Long managerId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, EmployeeManager> empJoin = root.join(Employee_.employees);
		Join<EmployeeManager, Employee> empMan = empJoin.join(EmployeeManager_.manager);
		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));
		predicates.add(criteriaBuilder.equal(empMan.get(Employee_.employeeId), managerId));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.select(criteriaBuilder.count(root));

		TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

		return query.getSingleResult();
	}

	@Override
	public Long countAllAvailableEmployeesLeavesByDate(LocalDate currentDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		Join<Employee, User> userJoin = root.join(Employee_.user);

		Subquery<Long> leaveSubquery = criteriaQuery.subquery(Long.class);
		Root<LeaveRequest> leaveRequestRoot = leaveSubquery.from(LeaveRequest.class);
		Join<LeaveRequest, Employee> leaveEmployeeJoin = leaveRequestRoot.join(LeaveRequest_.employee);

		Predicate subqueryEmployeePredicate = criteriaBuilder.equal(leaveEmployeeJoin.get(Employee_.employeeId),
				root.get(Employee_.employeeId));
		Predicate leaveDatePredicate = criteriaBuilder.and(
				criteriaBuilder.lessThanOrEqualTo(leaveRequestRoot.get(LeaveRequest_.startDate), currentDate),
				criteriaBuilder.greaterThanOrEqualTo(leaveRequestRoot.get(LeaveRequest_.endDate), currentDate));
		Predicate leaveStatusPredicate = leaveRequestRoot.get(LeaveRequest_.status)
			.in(LeaveRequestStatus.APPROVED, LeaveRequestStatus.PENDING);

		leaveSubquery.select(leaveEmployeeJoin.get(Employee_.employeeId))
			.where(criteriaBuilder.and(subqueryEmployeePredicate, leaveDatePredicate, leaveStatusPredicate));

		Subquery<Long> clockInSubquery = criteriaQuery.subquery(Long.class);
		Root<TimeRecord> timeRecordRoot = clockInSubquery.from(TimeRecord.class);
		Join<TimeRecord, Employee> timeRecordEmployeeJoin = timeRecordRoot.join(TimeRecord_.employee);

		Predicate clockInEmployeePredicate = criteriaBuilder.equal(timeRecordEmployeeJoin.get(Employee_.employeeId),
				root.get(Employee_.employeeId));
		Predicate clockInDatePredicate = criteriaBuilder.equal(timeRecordRoot.get(TimeRecord_.date), currentDate);

		clockInSubquery.select(timeRecordEmployeeJoin.get(Employee_.employeeId))
			.where(criteriaBuilder.and(clockInEmployeePredicate, clockInDatePredicate));

		List<Predicate> predicates = new ArrayList<>();
		Predicate isActivePredicate = criteriaBuilder.equal(userJoin.get(User_.isActive), true);
		predicates.add(isActivePredicate);

		Predicate notOnLeaveOrClockInPredicate = criteriaBuilder.or(
				criteriaBuilder.not(root.get(Employee_.employeeId).in(leaveSubquery)),
				root.get(Employee_.employeeId).in(clockInSubquery));
		predicates.add(notOnLeaveOrClockInPredicate);

		criteriaQuery.select(criteriaBuilder.countDistinct(root.get(Employee_.employeeId)));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getSingleResult();
	}

	@Override
	public List<Employee> findEmployeeByName(String keyword) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, User> userJoin = root.join(Employee_.user);
		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));

		String searchString = getSearchString(keyword);
		predicates.add(criteriaBuilder.or(
				criteriaBuilder.like(criteriaBuilder.lower(root.get(Employee_.FIRST_NAME)), searchString),
				criteriaBuilder.like(criteriaBuilder.lower(root.get(Employee_.LAST_NAME)), searchString),
				criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.concat(
						criteriaBuilder.concat(root.get(Employee_.FIRST_NAME), " "), root.get(Employee_.LAST_NAME))),
						searchString)));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.select(root).distinct(true);
		TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<Employee> findEmployeesByTeams(List<Long> teamIds) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, EmployeeTeam> employeeTeam = root.join(Employee_.TEAMS);
		Join<EmployeeTeam, Team> team = employeeTeam.join(EmployeeTeam_.TEAM);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(team.get(Team_.teamId).in(teamIds));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	private Predicate findByEmailName(String keyword, CriteriaBuilder criteriaBuilder, Root<Employee> employee,
			Join<Employee, User> userJoin) {
		keyword = getSearchString(keyword);
		return criteriaBuilder.or(
				criteriaBuilder.like(criteriaBuilder
					.lower(criteriaBuilder.concat(criteriaBuilder.concat(employee.get(Employee_.FIRST_NAME), " "),
							employee.get(Employee_.LAST_NAME))),
						keyword),
				criteriaBuilder.like(criteriaBuilder.lower(userJoin.get(User_.EMAIL)), keyword),
				criteriaBuilder.like(criteriaBuilder.lower(employee.get(Employee_.LAST_NAME)), keyword));
	}

	private void addEmploymentStatusPredicate(EmployeeFilterDto employeeFilterDto, CriteriaBuilder criteriaBuilder,
			Root<Employee> root, Join<Employee, User> userJoin, List<Predicate> predicates) {
		if (!employeeFilterDto.getAccountStatus().isEmpty()) {
			for (AccountStatus status : employeeFilterDto.getAccountStatus()) {
				if (status.equals(AccountStatus.ACTIVE)) {
					predicates.add(
							criteriaBuilder.equal(root.get(Employee_.ACCOUNT_STATUS), AccountStatus.ACTIVE.toString()));
				}
				if (status.equals(AccountStatus.PENDING)) {
					predicates.add(criteriaBuilder.equal(root.get(Employee_.ACCOUNT_STATUS),
							AccountStatus.PENDING.toString()));
				}
				if (status.equals(AccountStatus.TERMINATED)) {
					predicates.add(criteriaBuilder.or(criteriaBuilder.equal(userJoin.get(User_.isActive), false),
							criteriaBuilder.equal(root.get(Employee_.ACCOUNT_STATUS),
									AccountStatus.TERMINATED.toString())));
				}
			}
		}
	}

	private Page<Employee> findEmployeesByManagerId(Long managerId, Pageable page, boolean signInOnly) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> root = criteriaQuery.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();

		Join<Employee, EmployeeManager> empJoin = root.join(Employee_.employees);
		Join<EmployeeManager, Employee> empMan = empJoin.join(EmployeeManager_.manager);
		Join<Employee, User> userJoin = root.join(Employee_.user);

		predicates.add(criteriaBuilder.notEqual(userJoin.get(User_.isActive), false));
		predicates.add(criteriaBuilder.equal(empMan.get(Employee_.employeeId), managerId));

		if (signInOnly) {
			predicates.add(criteriaBuilder.equal(root.get(Employee_.ACCOUNT_STATUS), AccountStatus.ACTIVE));
		}

		Predicate[] predArray = predicates.toArray(new Predicate[0]);
		criteriaQuery.where(predArray);
		criteriaQuery.orderBy(QueryUtils.toOrders(page.getSort(), root, criteriaBuilder));

		TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);
		int totalRows = query.getResultList().size();
		query.setFirstResult(page.getPageNumber() * page.getPageSize());
		query.setMaxResults(page.getPageSize());

		return new PageImpl<>(query.getResultList(), page, totalRows);
	}

}
