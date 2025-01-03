package com.skapp.community.peopleplanner.service.impl;

import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.repository.UserDao;
import com.skapp.community.common.service.UserService;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.leaveplanner.type.ManagerType;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import com.skapp.community.peopleplanner.model.EmployeeTimeline;
import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.model.JobTitle;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeProgressionsDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeTimelineResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeTimelineResponseListDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeTimelineDao;
import com.skapp.community.peopleplanner.repository.JobFamilyDao;
import com.skapp.community.peopleplanner.repository.JobTitleDao;
import com.skapp.community.peopleplanner.service.EmployeeTimelineService;
import com.skapp.community.peopleplanner.type.EmployeeTimelineType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_EMPLOYMENT_ALLOCATION_ADDED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_EMPLOYMENT_TYPE_ADDED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_JOB_FAMILY_ASSIGNED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_JOB_TITLE_ASSIGNED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_JOINED_DATE;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_PRIMARY_MANAGER_ASSIGNED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_PROBATION_END_DATE_ADDED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_PROBATION_START_DATE_ADDED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_SECONDARY_MANAGER_ASSIGNED;
import static com.skapp.community.peopleplanner.constant.EmployeeTimelineConstant.TITLE_TEAM_ASSIGNED;
import static com.skapp.community.peopleplanner.constant.PeopleMessageConstant.PEOPLE_ERROR_RESOURCE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeTimelineServiceImpl implements EmployeeTimelineService {

	@NonNull
	private final EmployeeTimelineDao employeeTimelineDao;

	@NonNull
	private final UserService userService;

	@NonNull
	private final EmployeeDao employeeDao;

	@NonNull
	private final UserDao userDao;

	@NonNull
	private final PeopleMapper peopleMapper;

	@NonNull
	private final JobTitleDao jobTitleDao;

	@NonNull
	private final JobFamilyDao jobFamilyDao;

	@Override
	public void addEmployeeTimelineRecord(Employee employee, EmployeeTimelineType timelineType, String title,
			String previousValue, String newValue) {
		log.info("addEmployeeTimelineRecord: execution started");

		EmployeeTimeline employeeTimeline = getEmployeeTimeline(employee, timelineType, title, previousValue, newValue);
		employeeTimelineDao.save(employeeTimeline);

		log.info("addEmployeeTimelineRecord: execution ended");
	}

	@Override
	public ResponseEntityDto getEmployeeTimelineRecords(Long id) {
		User currentUser = userService.getCurrentUser();
		log.info("getCurrentEmployeeTimelineRecords: execution started by user: {}", currentUser.getUserId());

		Optional<Employee> employeeOptional = employeeDao.findById(id);
		if (employeeOptional.isEmpty()) {
			throw new ModuleException(PEOPLE_ERROR_RESOURCE_NOT_FOUND);
		}

		List<EmployeeTimeline> employeeTimelines = employeeTimelineDao.findAllByEmployee(employeeOptional.get());

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-yyyy", Locale.ENGLISH);
		Map<String, List<EmployeeTimeline>> groupedTimelines = employeeTimelines.stream()
			.filter(timeline -> timeline.getDisplayDate() != null)
			.collect(Collectors.groupingBy(timeline -> timeline.getDisplayDate().format(dateFormatter)));

		List<EmployeeTimelineResponseListDto> employeeTimelineResponseListDtos = groupedTimelines.entrySet()
			.stream()
			.map(entry -> {
				String yearMonth = entry.getKey();
				List<EmployeeTimeline> timelinesForYearMonth = entry.getValue();

				EmployeeTimelineResponseListDto responseDtoList = new EmployeeTimelineResponseListDto();

				if (yearMonth != null && yearMonth.contains("-")) {
					String[] parts = yearMonth.split("-");
					try {
						responseDtoList.setYear((long) Integer.parseInt(parts[1]));
						responseDtoList.setMonth(parts[0]);
					}
					catch (NumberFormatException e) {
						log.error("Failed to parse year and month from yearMonth: {}", yearMonth, e);
						responseDtoList.setYear(null);
						responseDtoList.setMonth(null);
					}
				}
				else {
					log.warn("Invalid yearMonth format: {}", yearMonth);
					responseDtoList.setYear(null);
					responseDtoList.setMonth(null);
				}

				List<EmployeeTimelineResponseDto> unsortedTimeLines = peopleMapper
					.employeeTimelinesToEmployeeTimelineResponseDtoList(timelinesForYearMonth);
				unsortedTimeLines.forEach(timelineObj -> {
					String createdById = timelineObj.getCreatedBy();
					if (createdById != null && !createdById.isEmpty()) {
						try {
							Optional<User> userOpt = userDao.findById(Long.parseLong(createdById));
							if (userOpt.isPresent() && userOpt.get().getEmployee() != null
									&& userOpt.get().getEmployee().getFirstName() != null) {
								Employee createdEmployee = userOpt.get().getEmployee();
								String createdBy = createdEmployee.getFirstName()
									.concat(createdEmployee.getLastName() != null ? " " + createdEmployee.getLastName()
											: " " + createdEmployee.getMiddleName());
								timelineObj.setCreatedBy(createdBy);
							}
						}
						catch (NumberFormatException e) {
							log.error("Invalid createdBy ID: {}", createdById, e);
							timelineObj.setCreatedBy("Unknown");
						}
					}
					else {
						timelineObj.setCreatedBy("Unknown");
					}
				});

				unsortedTimeLines.sort(
						Comparator.comparing(EmployeeTimelineResponseDto::getDisplayDate, Collections.reverseOrder()));
				responseDtoList.setEmployeeTimelineRecords(unsortedTimeLines);

				return responseDtoList;
			})
			.sorted(Comparator
				.comparing(EmployeeTimelineResponseListDto::getYear, Comparator.nullsLast(Collections.reverseOrder()))
				.thenComparing(EmployeeTimelineResponseListDto::getMonth,
						Comparator.nullsLast(Collections.reverseOrder())))
			.toList();

		log.info("getCurrentEmployeeTimelineRecords: execution ended by user: {}", currentUser.getUserId());
		return new ResponseEntityDto(false, employeeTimelineResponseListDtos);
	}

	private EmployeeTimeline getEmployeeTimeline(Employee employee, EmployeeTimelineType timelineType, String title,
			String previousValue, String newValue) {
		EmployeeTimeline employeeTimeline = new EmployeeTimeline();
		employeeTimeline.setEmployee(employee);
		employeeTimeline.setTimelineType(timelineType);
		employeeTimeline.setTitle(title);
		employeeTimeline.setPreviousValue(previousValue);
		employeeTimeline.setNewValue(newValue);
		employeeTimeline.setDisplayDate(DateTimeUtils.getCurrentUtcDate());
		return employeeTimeline;
	}

	@Override
	public void addNewEmployeeTimeLineRecords(Employee employee, EmployeeDetailsDto employeeDetailsDto) {
		List<EmployeeTimeline> employeeTimelines = new ArrayList<>();

		addJobProgressionTimeline(employee, employeeDetailsDto, employeeTimelines);
		addJoinDateTimeline(employee, employeeTimelines);
		addProbationDateTimeline(employee, employeeDetailsDto, employeeTimelines);
		addTeamTimeline(employee, employeeTimelines);
		addManagerTimeline(employee, employeeTimelines);
		addEmploymentTypeTimeline(employee, employeeDetailsDto, employeeTimelines);
		addEmploymentAllocationTimeline(employee, employeeTimelines);

		employeeTimelineDao.saveAll(employeeTimelines);
	}

	private void addJobProgressionTimeline(Employee employee, EmployeeDetailsDto employeeDetailsDto,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeDetailsDto.getEmployeeProgressions() != null
				&& !employeeDetailsDto.getEmployeeProgressions().isEmpty()) {
			if (employeeDetailsDto.getEmployeeProgressions().getFirst().getJobTitleId() != null) {
				Optional<JobTitle> jobTitle = jobTitleDao
					.findById(employeeDetailsDto.getEmployeeProgressions().getFirst().getJobTitleId());
				jobTitle.ifPresent(title -> employeeTimelines.add(getEmployeeTimeline(employee,
						EmployeeTimelineType.JOB_LEVEL_ASSIGNED, TITLE_JOB_TITLE_ASSIGNED, null, title.getName())));

			}
			if (employeeDetailsDto.getEmployeeProgressions().getFirst().getJobFamilyId() != null) {
				Optional<JobFamily> jobFamily = jobFamilyDao
					.findById(employeeDetailsDto.getEmployeeProgressions().getFirst().getJobFamilyId());
				jobFamily.ifPresent(family -> employeeTimelines.add(getEmployeeTimeline(employee,
						EmployeeTimelineType.JOB_FAMILY_ASSIGNED, TITLE_JOB_FAMILY_ASSIGNED, null, family.getName())));

			}
		}
	}

	private void addJoinDateTimeline(Employee employee, List<EmployeeTimeline> employeeTimelines) {
		EmployeeTimeline empJoinDateRecord = getEmployeeTimeline(employee, EmployeeTimelineType.JOINED_DATE,
				TITLE_JOINED_DATE, null, employee.getJoinDate() != null ? employee.getJoinDate().toString() : null);

		if (employee.getJoinDate() != null) {
			empJoinDateRecord.setDisplayDate(DateTimeUtils.getCurrentUtcDate());

		}

		employeeTimelines.add(empJoinDateRecord);
	}

	private void addEmploymentTypeTimeline(Employee employee, EmployeeDetailsDto employeeDetailsDto,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeDetailsDto != null && employeeDetailsDto.getEmployeeProgressions() != null
				&& !employeeDetailsDto.getEmployeeProgressions().isEmpty()
				&& employeeDetailsDto.getEmployeeProgressions().getFirst().getEmployeeType() != null) {

			EmployeeTimeline employmentAllocationRecord = getEmployeeTimeline(employee,
					EmployeeTimelineType.EMPLOYMENT_TYPE_ADDED, TITLE_EMPLOYMENT_TYPE_ADDED, null,
					employeeDetailsDto.getEmployeeProgressions().getFirst().getEmployeeType().toString());
			employeeTimelines.add(employmentAllocationRecord);
		}
	}

	public void addEmploymentAllocationTimeline(Employee employee, List<EmployeeTimeline> employeeTimelines) {
		if (employee.getEmploymentAllocation() != null) {
			EmployeeTimeline employmentAllocationRecord = getEmployeeTimeline(employee,
					EmployeeTimelineType.EMPLOYMENT_ALLOCATION_ADDED, TITLE_EMPLOYMENT_ALLOCATION_ADDED, null,
					employee.getEmploymentAllocation().toString());
			employeeTimelines.add(employmentAllocationRecord);
		}
	}

	private void addProbationDateTimeline(Employee employee, EmployeeDetailsDto employeeDetailsDto,
			List<EmployeeTimeline> employeeTimelines) {
		if (employeeDetailsDto.getProbationPeriod().getStartDate() != null) {
			EmployeeTimeline empProbationDateRecord = getEmployeeTimeline(employee,
					EmployeeTimelineType.PROBATION_START_DATE, TITLE_PROBATION_START_DATE_ADDED, null,
					employeeDetailsDto.getProbationPeriod().getStartDate().toString());
			employeeTimelines.add(empProbationDateRecord);
		}
		if (employeeDetailsDto.getProbationPeriod().getEndDate() != null) {
			EmployeeTimeline empProbationDateRecord = getEmployeeTimeline(employee,
					EmployeeTimelineType.PROBATION_END_DATE, TITLE_PROBATION_END_DATE_ADDED, null,
					employeeDetailsDto.getProbationPeriod().getEndDate().toString());
			employeeTimelines.add(empProbationDateRecord);
		}
	}

	private void addTeamTimeline(Employee employee, List<EmployeeTimeline> employeeTimelines) {
		if (employee.getTeams() != null && !employee.getTeams().isEmpty()) {
			employee.getTeams()
				.forEach(empTeam -> employeeTimelines
					.add(getEmployeeTimeline(employee, EmployeeTimelineType.TEAM_ASSIGNED, TITLE_TEAM_ASSIGNED, null,
							empTeam.getTeam().getTeamName())));
		}
	}

	private void addManagerTimeline(Employee employee, List<EmployeeTimeline> employeeTimelines) {
		if (employee.getManagers() != null && !employee.getManagers().isEmpty()) {
			employee.getManagers().forEach(empManager -> {
				String managerTypeTitle = getManagerTypeTitle(empManager);
				if (managerTypeTitle != null) {
					employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.MANAGER_ASSIGNED,
							managerTypeTitle, null,
							empManager.getManager().getFirstName() + " " + empManager.getManager().getLastName()));
				}
			});
		}
	}

	private String getManagerTypeTitle(EmployeeManager empManager) {
		if (empManager.getManagerType() == ManagerType.PRIMARY) {
			return TITLE_PRIMARY_MANAGER_ASSIGNED;
		}
		else if (empManager.getManagerType() == ManagerType.SECONDARY) {
			return TITLE_SECONDARY_MANAGER_ASSIGNED;
		}
		return null;
	}

	private Optional<EmployeeProgressionsDto> getCurrentCareer(List<EmployeeProgressionsDto> careerProgressions) {
		return careerProgressions.stream().filter(EmployeeProgressionsDto::getIsCurrent).findFirst();
	}

}
