package com.skapp.community.peopleplanner.mapper;

import com.skapp.community.common.payload.request.SuperAdminSignUpRequestDto;
import com.skapp.community.common.payload.response.EmployeeSignInResponseDto;
import com.skapp.community.leaveplanner.payload.EmployeeLeaveEntitlementsDto;
import com.skapp.community.leaveplanner.payload.EmployeeLeaveRequestDto;
import com.skapp.community.leaveplanner.payload.EmployeeSummarizedResponseDto;
import com.skapp.community.leaveplanner.payload.ManagerSummarizedTeamResponseDto;
import com.skapp.community.leaveplanner.payload.response.EmployeeLeaveEntitlementReportExportDto;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeEducation;
import com.skapp.community.peopleplanner.model.EmployeeEmergency;
import com.skapp.community.peopleplanner.model.EmployeeFamily;
import com.skapp.community.peopleplanner.model.EmployeeManager;
import com.skapp.community.peopleplanner.model.EmployeePeriod;
import com.skapp.community.peopleplanner.model.EmployeePersonalInfo;
import com.skapp.community.peopleplanner.model.EmployeeProgression;
import com.skapp.community.peopleplanner.model.EmployeeRole;
import com.skapp.community.peopleplanner.model.EmployeeTeam;
import com.skapp.community.peopleplanner.model.EmployeeTimeline;
import com.skapp.community.peopleplanner.model.EmployeeVisa;
import com.skapp.community.peopleplanner.model.Holiday;
import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.model.JobTitle;
import com.skapp.community.peopleplanner.model.ModuleRoleRestriction;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeBulkDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeDetailsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeEducationDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeEmergencyDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeFamilyDto;
import com.skapp.community.peopleplanner.payload.request.EmployeePersonalInfoDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeProgressionsDto;
import com.skapp.community.peopleplanner.payload.request.EmployeeQuickAddDto;
import com.skapp.community.peopleplanner.payload.request.EmploymentVisaDto;
import com.skapp.community.peopleplanner.payload.request.HolidayRequestDto;
import com.skapp.community.peopleplanner.payload.request.JobFamilyDto;
import com.skapp.community.peopleplanner.payload.request.JobTitleDto;
import com.skapp.community.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.skapp.community.peopleplanner.payload.request.TeamRequestDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeDataExportResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeDetailedResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeJobFamilyDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeManagerResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeePeriodResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeRoleResponseDto;
import com.skapp.community.peopleplanner.payload.response.EmployeeTimelineResponseDto;
import com.skapp.community.peopleplanner.payload.response.HolidayBasicDetailsResponseDto;
import com.skapp.community.peopleplanner.payload.response.HolidayResponseDto;
import com.skapp.community.peopleplanner.payload.response.JobFamilyResponseDetailDto;
import com.skapp.community.peopleplanner.payload.response.JobFamilyResponseDto;
import com.skapp.community.peopleplanner.payload.response.JobTitleResponseDetailDto;
import com.skapp.community.peopleplanner.payload.response.ManagerCoreDetailsDto;
import com.skapp.community.peopleplanner.payload.response.ManagerEmployeeDto;
import com.skapp.community.peopleplanner.payload.response.ManagingEmployeesResponseDto;
import com.skapp.community.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;
import com.skapp.community.peopleplanner.payload.response.SummarizedEmployeeDtoForEmployees;
import com.skapp.community.peopleplanner.payload.response.SummarizedManagerEmployeeDto;
import com.skapp.community.peopleplanner.payload.response.TeamBasicDetailsResponseDto;
import com.skapp.community.peopleplanner.payload.response.TeamDetailResponseDto;
import com.skapp.community.peopleplanner.payload.response.TeamEmployeeResponseDto;
import com.skapp.community.peopleplanner.payload.response.TeamResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PeopleMapper {

	Employee createSuperAdminRequestDtoToEmployee(SuperAdminSignUpRequestDto superAdminSignUpRequestDto);

	EmployeeResponseDto employeeToEmployeeResponseDto(Employee employee);

	Team teamRequestDtoToTeam(TeamRequestDto teamRequestDto);

	TeamResponseDto teamToTeamResponseDto(Team team);

	List<TeamResponseDto> teamListToTeamResponseDtoList(List<Team> teams);

	Holiday holidayDtoToHoliday(HolidayRequestDto holidayRequestDto);

	HolidayResponseDto holidayToHolidayResponseDto(Holiday holiday);

	List<HolidayResponseDto> holidaysToHolidayResponseDtoList(List<Holiday> holidays);

	List<JobFamilyResponseDetailDto> jobFamilyListToJobFamilyResponseDetailDtoList(List<JobFamily> jobFamilies);

	JobFamilyResponseDetailDto jobFamilyToJobFamilyResponseDetailDto(JobFamily jobFamily);

	JobTitleResponseDetailDto jobTitleToJobTitleResponseDetailDto(JobTitle jobTitle);

	JobFamilyResponseDto jobFamilyToJobFamilyResponseDto(JobFamily jobFamily);

	JobFamily jobFamilyDtoToJobFamily(JobFamilyDto jobFamilyDto);

	JobFamilyDto jobFamilyToJobFamilyDto(JobFamily jobFamily);

	@Mapping(target = "user.email", source = "employeeDetailsDto.workEmail")
	@Mapping(target = "firstName", source = "employeeDetailsDto.firstName")
	@Mapping(target = "managers", ignore = true)
	@Mapping(target = "teams", ignore = true)
	@Mapping(target = "employeeProgressions", ignore = true)
	@Mapping(target = "employeeVisas", ignore = true)
	Employee employeeDetailsDtoToEmployee(EmployeeDetailsDto employeeDetailsDto);

	EmployeeProgression employeeProgressionDtoToEmployeeProgression(EmployeeProgressionsDto employeeProgressionsDto);

	List<EmployeeEmergency> employeeEmergencyDtoListToEmployeeEmergencyList(
			List<EmployeeEmergencyDto> employeeEmergencyDto);

	EmployeePersonalInfo employeePersonalInfoDtoToEmployeePersonalInfo(EmployeePersonalInfoDto employeePersonalInfoDto);

	List<EmployeeVisa> employeeVisaDtoListToEmployeeVisaList(List<EmploymentVisaDto> employmentVisa);

	List<EmployeeEducation> employeeEducationDtoListToEmployeeEducationList(
			List<EmployeeEducationDto> employeeEducations);

	List<EmployeeFamily> employeeFamilyDtoListToEmployeeFamilyList(List<EmployeeFamilyDto> employeeFamilies);

	@Mapping(target = "email", source = "user.email")
	@Mapping(target = "isActive", source = "user.isActive")
	@Mapping(target = "managers", source = "employees")
	EmployeeDetailedResponseDto employeeToEmployeeDetailedResponseDto(Employee employee);

	EmployeePeriodResponseDto employeePeriodToEmployeePeriodResponseDto(EmployeePeriod employeePeriod);

	EmployeeEducation employeeEducationToEmployeeEducation(EmployeeEducationDto employeeEducationDto);

	EmployeeVisa employeeVisaDtoToEmployeeVisa(EmploymentVisaDto visa);

	EmployeeFamily employeeFamilyDtoToEmployeeFamily(EmployeeFamilyDto employeeFamilyDto);

	@Mapping(target = "email", source = "user.email")
	@Mapping(target = "isActive", source = "user.isActive")
	@Mapping(target = "teamResponseDto", ignore = true)
	@Mapping(target = "jobFamily", ignore = true)
	@Mapping(target = "managers", ignore = true)
	EmployeeDataExportResponseDto employeeToEmployeeDataExportResponseDto(Employee employee);

	List<EmployeeResponseDto> employeeListToEmployeeResponseDtoList(List<Employee> employee);

	EmployeeJobFamilyDto jobFamilyToEmployeeJobFamilyDto(JobFamily jobFamily);

	@Mapping(target = "email", source = "user.email")
	ManagerEmployeeDto employeeToManagerEmployeeDto(Employee employee);

	@Mapping(target = "email", source = "user.email")
	SummarizedManagerEmployeeDto employeeToSummarizedManagerEmployeeDto(Employee employee);

	@Mapping(target = "email", source = "user.email")
	SummarizedEmployeeDtoForEmployees employeeToSummarizedEmployeeDtoForEmployees(Employee employee);

	List<TeamEmployeeResponseDto> employeeTeamToTeamEmployeeResponseDto(Set<EmployeeTeam> teams);

	@Mapping(target = "isSuperAdmin", source = "isSuperAdmin")
	EmployeeRoleResponseDto employeeRoleToEmployeeRoleResponseDto(EmployeeRole employeeRole);

	List<ManagingEmployeesResponseDto> employeeManagerToManagingEmployeesResponseDto(
			Set<EmployeeManager> employeeManager);

	@Mapping(target = "user.email", source = "employeeBulkDto.workEmail")
	@Mapping(target = "firstName", source = "employeeBulkDto.firstName")
	@Mapping(target = "teams", ignore = true)
	@Mapping(target = "joinDate", source = "employeeBulkDto.joinedDate")
	@Mapping(target = "jobFamily", ignore = true)
	@Mapping(target = "jobTitle", ignore = true)
	Employee employeeBulkDtoToEmployee(EmployeeBulkDto employeeBulkDto);

	@Mapping(target = "primaryManager", ignore = true)
	@Mapping(target = "secondaryManager", ignore = true)
	@Mapping(target = "teams", ignore = true)
	@Mapping(target = "joinDate", source = "employeeBulkDto.joinedDate")
	@Mapping(target = "employeeEmergency", ignore = true)
	@Mapping(target = "employeePersonalInfo.birthDate", dateFormat = "yyyy-MM-dd")
	EmployeeDetailsDto employeeBulkDtoToEmployeeDetailsDto(EmployeeBulkDto employeeBulkDto);

	EmployeeEmergency employeeEmergencyDtoToEmployeeEmergency(EmployeeEmergencyDto employeeEmergency);

	Employee employeeQuickAddDtoToEmployee(EmployeeQuickAddDto employeeQuickAddDto);

	ModuleRoleRestriction roleRestrictionRequestDtoToRestrictRole(
			ModuleRoleRestrictionRequestDto moduleRoleRestrictionRequestDto);

	ModuleRoleRestrictionResponseDto restrictRoleToRestrictRoleResponseDto(ModuleRoleRestriction restrictedRole);

	List<EmployeeDetailedResponseDto> employeeListToEmployeeDetailedResponseDtoList(List<Employee> employees);

	@Mapping(target = "id", source = "timeline_id")
	List<EmployeeTimelineResponseDto> employeeTimelinesToEmployeeTimelineResponseDtoList(
			List<EmployeeTimeline> employeeTimelines);

	EmployeeLeaveEntitlementsDto employeeLeaveEntitlementTeamJobRoleToEmployeeLeaveEntitlementsDto(
			EmployeeLeaveEntitlementReportExportDto etj);

	List<ManagerSummarizedTeamResponseDto> managerTeamsToManagerTeamCountTeamResponseDto(
			List<Team> managerLeadingTeams);

	ManagerCoreDetailsDto employeeToManagerCoreDetailsDto(Employee employee);

	@Mapping(target = "jobFamily", source = "jobFamily.name")
	@Mapping(target = "jobTitle", source = "jobTitle.name")
	EmployeeSignInResponseDto employeeToEmployeeSignInResponseDto(Employee employee);

	EmployeeBasicDetailsResponseDto employeeToEmployeeBasicDetailsResponseDto(Employee employee);

	List<HolidayBasicDetailsResponseDto> holidaysToHolidayBasicDetailsResponseDtos(List<Holiday> holidays);

	List<EmployeeBasicDetailsResponseDto> employeeLeaveRequestDtosToEmployeeBasicDetailsResponseDtos(
			List<EmployeeLeaveRequestDto> onLeaveEmployees);

	List<EmployeeBasicDetailsResponseDto> employeesToEmployeeBasicDetailsResponseDtos(List<Employee> employees);

	JobTitleDto jobTitleToJobTitleDto(JobTitle jobTitle);

	List<EmployeeManagerResponseDto> employeeManagerListToEmployeeManagerResponseDtoList(
			List<EmployeeManager> byEmployee);

	List<EmployeeSummarizedResponseDto> employeeListToEmployeeSummarizedResponseDto(List<Employee> employee);

	List<TeamDetailResponseDto> teamToTeamDetailResponseDto(List<Team> team);

	List<TeamBasicDetailsResponseDto> teamListToTeamBasicDetailsResponseDtoList(List<Team> teams);

}
