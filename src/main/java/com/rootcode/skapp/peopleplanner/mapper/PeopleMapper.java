package com.rootcode.skapp.peopleplanner.mapper;

import com.rootcode.skapp.common.payload.request.SuperAdminSignUpRequestDto;
import com.rootcode.skapp.common.payload.response.EmployeeSignInResponseDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeLeaveEntitlementsDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeLeaveRequestDto;
import com.rootcode.skapp.leaveplanner.payload.EmployeeSummarizedResponseDto;
import com.rootcode.skapp.leaveplanner.payload.ManagerSummarizedTeamResponseDto;
import com.rootcode.skapp.leaveplanner.payload.response.EmployeeLeaveEntitlementReportExportDto;
import com.rootcode.skapp.peopleplanner.model.Employee;
import com.rootcode.skapp.peopleplanner.model.EmployeeEducation;
import com.rootcode.skapp.peopleplanner.model.EmployeeEmergency;
import com.rootcode.skapp.peopleplanner.model.EmployeeFamily;
import com.rootcode.skapp.peopleplanner.model.EmployeeManager;
import com.rootcode.skapp.peopleplanner.model.EmployeePeriod;
import com.rootcode.skapp.peopleplanner.model.EmployeePersonalInfo;
import com.rootcode.skapp.peopleplanner.model.EmployeeProgression;
import com.rootcode.skapp.peopleplanner.model.EmployeeRole;
import com.rootcode.skapp.peopleplanner.model.EmployeeTeam;
import com.rootcode.skapp.peopleplanner.model.EmployeeTimeline;
import com.rootcode.skapp.peopleplanner.model.EmployeeVisa;
import com.rootcode.skapp.peopleplanner.model.Holiday;
import com.rootcode.skapp.peopleplanner.model.JobFamily;
import com.rootcode.skapp.peopleplanner.model.JobTitle;
import com.rootcode.skapp.peopleplanner.model.ModuleRoleRestriction;
import com.rootcode.skapp.peopleplanner.model.Team;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeBulkDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeDetailsDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEducationDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEmergencyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeePersonalInfoDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeProgressionsDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeQuickAddDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmploymentVisaDto;
import com.rootcode.skapp.peopleplanner.payload.request.HolidayRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.JobFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.request.ModuleRoleRestrictionRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.TeamRequestDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeDataExportResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeDetailedResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeJobFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeManagerResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeePeriodResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeRoleResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.EmployeeTimelineResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayBasicDetailsResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.HolidayResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.JobFamilyResponseDetailDto;
import com.rootcode.skapp.peopleplanner.payload.response.JobFamilyResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.JobTitleDto;
import com.rootcode.skapp.peopleplanner.payload.response.JobTitleResponseDetailDto;
import com.rootcode.skapp.peopleplanner.payload.response.ManagerCoreDetailsDto;
import com.rootcode.skapp.peopleplanner.payload.response.ManagerEmployeeDto;
import com.rootcode.skapp.peopleplanner.payload.response.ManagingEmployeesResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.ModuleRoleRestrictionResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.SummarizedEmployeeDtoForEmployees;
import com.rootcode.skapp.peopleplanner.payload.response.SummarizedManagerEmployeeDto;
import com.rootcode.skapp.peopleplanner.payload.response.TeamBasicDetailsResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.TeamDetailResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.TeamEmployeeResponseDto;
import com.rootcode.skapp.peopleplanner.payload.response.TeamResponseDto;
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
