package com.skapp.community.peopleplanner.constant;

import com.skapp.community.common.constant.MessageConstant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeopleMessageConstant implements MessageConstant {

	// Success messages
	PEOPLE_SUCCESS_DELETE_JOB_FAMILY("api.success.people.delete-job-family"),
	PEOPLE_SUCCESS_DELETE_JOB_TITLE("api.success.people.delete-job-title"),
	PEOPLE_SUCCESS_TRANSFER_JOB_TITLE("api.success.people.transfer-job-title"),
	PEOPLE_SUCCESS_TRANSFER_JOB_FAMILY("api.success.people.transfer-job-family"),
	PEOPLE_SUCCESS_ROLE_RESTRICT("api.success.people.role-restrict"),
	PEOPLE_SUCCESS_TEAM_DELETED("api.success.people.team-deleted"),
	PEOPLE_SUCCESS_EMPLOYEE_ADDED("api.success.people.employee-added"),

	// Error messages
	PEOPLE_ERROR_TEAM_SUPERVISOR_IDS_NOT_VALID("api.error.people.notnull.team.supervisors.invalid"),
	PEOPLE_ERROR_TEAM_MEMBER_IDS_NOT_VALID("api.error.people.notnull.team.members.invalid"),
	PEOPLE_ERROR_TEAM_NAME_ALREADY_EXISTS("api.error.people.team.name.already.exist"),
	PEOPLE_ERROR_TEAM_SUPERVISOR_COUNT_MORE_THAN_THREE("api.error.people.team.supervisor.more-than-three"),
	PEOPLE_ERROR_DELETE_HOLIDAYS_ARRAY_EMPTY("api.error.people.delete-holidays.array.empty"),
	PEOPLE_ERROR_SPECIFIC_HOLIDAY_NOT_FOUND("api.error.people.specific.holiday.not-found"),
	PEOPLE_ERROR_HOLIDAY_CANNOT_BE_DELETED("api.error.people.holiday.cannot.delete"),
	PEOPLE_ERROR_HOLIDAY_CANNOT_BE_DELETED_LEAVES_EXIST("api.error.people.holiday.cannot.delete.leaves.exist"),
	PEOPLE_ERROR_HOLIDAYS_BULK_CANNOT_ADDED_IN_PAST("api.error.people.holiday-bulk.cannot.add.past.days"),
	PEOPLE_ERROR_TEAM_NOT_FOUND("api.error.people.people.team.not.found"),
	PEOPLE_ERROR_EMPLOYEE_NOT_FOUND("api.error.people.employee.not.found"),
	PEOPLE_ERROR_ONLY_ONE_SUPER_ADMIN("api.error.people.super-admin.only-one-super-admin"),
	PEOPLE_ERROR_HOLIDAYS_BULK_CANNOT_ADDED_IN_PAST_OR_CURRENT_DAY(
			"api.error.people.holiday-bulk.cannot.add.current.or.past.days"),
	PEOPLE_ERROR_HOLIDAYS_BULK_ONLY_ADDED_FUTURE_DAYS("api.error.people.holiday-bulk.added.only.future.days"),
	PEOPLE_ERROR_HOLIDAY_BULK_FAILED_TO_ADD_ANY("api.error.people.holiday-bulk.failed.to.add.any"),
	PEOPLE_ERROR_HOLIDAYS_BULK_ADDED_SUCCESSFULLY("api.error.people.holiday-bulk.added.successfully"),
	PEOPLE_ERROR_JOB_FAMILY_NOT_FOUND("api.error.people.job.family.not.found"),
	PEOPLE_ERROR_JOB_TITLE_NOT_FOUND("api.error.people.job.title.not.found"),
	PEOPLE_ERROR_JOB_FAMILY_INSUFFICIENT_DATA("api.error.people.job.family.insufficient.data"),
	PEOPLE_ERROR_JOB_FAMILY_AND_JOB_TITLE_NAME_INVALID("api.error.people.job.family.and.job.title.name.invalid"),
	PEOPLE_ERROR_JOB_FAMILY_EMPLOYEE_MISMATCH("api.error.people.job.family.employee.mismatch"),
	PEOPLE_ERROR_JOB_FAMILY_JOB_TITLE_EMPLOYEE_MISMATCH("api.error.people.job.family.job.title.employee.mismatch"),
	PEOPLE_ERROR_JOB_FAMILY_CHANGED("api.error.people.job.family.family.changed"),
	PEOPLE_ERROR_JOB_TITLE_CHANGED("api.error.people.job.family.title.changed"),
	PEOPLE_ERROR_JOB_FAMILY_REQUEST_EMPTY("api.error.people.job-family-request-empty"),
	PEOPLE_ERROR_JOB_TITLE_REQUEST_EMPTY("api.error.people.job-title-request-empty"),
	PEOPLE_ERROR_JOB_FAMILY_AND_JOB_TITLE_NOT_MATCH("api.error.people.job.family.and.job.title.not.match"),
	PEOPLE_ERROR_LEADING_TEAMS("api.error.people.leading-teams"),
	PEOPLE_ERROR_SUPERVISING_EMPLOYEES("api.error.people.supervising-employees"),
	PEOPLE_ERROR_SUPERVISOR_NOT_FOUND("api.error.people.supervisor.not.found"),
	PEOPLE_ERROR_SECONDARY_MANAGER_DUPLICATE("api.error.people.secondary.manager.duplicate"),
	PEOPLE_ERROR_INVALID_PHONE_NUMBER("api.error.people.employee.invalid-phone-number"),
	PEOPLE_ERROR_INVALID_IDENTIFICATION_NUMBER("api.error.people.employee.invalid-identification-number"),
	PEOPLE_ERROR_USER_EMAIL_ALREADY_EXIST("api.error.people.employee.user.email.exist"),
	PEOPLE_ERROR_RESOURCE_NOT_FOUND("api.error.people.employee.resource.not-found"),
	PEOPLE_ERROR_INVALID_START_END_DATE("api.error.people.employee.invalid.dates"),
	PEOPLE_ERROR_USER_NOT_HAVING_MANAGER_PERMISSIONS("api.error.people.manager-permission-not.found"),
	PEOPLE_ERROR_DUPLICATE_IDENTIFICATION_NO("api.error.people.parameter.duplicate.identification.no"),
	PEOPLE_ERROR_USER_ENTITLEMENT_BULK_UPLOAD_VALIDATION_FAILED(
			"api.error.people.user-entitlement-bulk-upload.validation.failed"),
	PEOPLE_ERROR_MISSING_USER_BULK_MANDATORY_FIELDS("api.error.people.missing.user.bulk.mandatory.fields"),
	PEOPLE_ERROR_INVALID_TIMEZONE("app.error.people.employee.invalid.timezone"),
	PEOPLE_ERROR_INVALID_EMAIL("app.error.people.employee.invalid.email"),
	PEOPLE_ERROR_SUPER_ADMIN_RESTRICTED_ASSIGNING_ROLE_ACCESS(
			"api.error.people.super-admin-restricted-assigning-role-access"),
	PEOPLE_ERROR_SHOULD_ASSIGN_PROPER_PERMISSIONS("api.error.people.dont-have-proper-permissions"),
	PEOPLE_ERROR_NO_ACTIVE_JOB_TITLES("api.error.people.no-active-job-titles"),
	PEOPLE_ERROR_EMPLOYEE_NOT_UNDER_CURRENT_EMPLOYEE_SUPERVISION(
			"api.error.people.employee.not-under-current-employee-supervision"),
	PEOPLE_ERROR_USER_ID_DOES_NOT_MATCH("api.error.people.user-id-does-not-match"),
	PEOPLE_ERROR_USER_IS_NOT_SUPERVISOR_FOR_SELECTED_TEAMS("api.error.people.is-not-supervisor-for-selected-teams"),
	PEOPLE_ERROR_HOLIDAY_MAXIMUM_PER_DAY("api.error.people.holiday.maximum-per-day"),
	PEOPLE_ERROR_HOLIDAY_REQUIRED_DATE("api.error.people.holiday.required-date"),
	PEOPLE_ERROR_HOLIDAY_TODAY_NOT_ALLOWED("api.error.people.holiday.today-not-allowed"),
	PEOPLE_ERROR_HOLIDAY_PAST_DATE_NOT_ALLOWED("api.error.people.holiday.past-date-not-allowed"),
	PEOPLE_ERROR_HOLIDAY_NAME_REQUIRED("api.error.people.holiday.name-required"),
	PEOPLE_ERROR_HOLIDAY_NAME_CHAR_LIMIT("api.error.people.holiday.name-char-limit"),
	PEOPLE_ERROR_HOLIDAY_NAME_SPECIAL_CHAR("api.error.people.holiday.name-special-char"),
	PEOPLE_ERROR_HOLIDAY_DURATION_INVALID("api.error.people.holiday.duration-invalid"),
	PEOPLE_ERROR_NO_MANAGERS_FOUND("api.error.people.no-managers-found"),
	PEOPLE_ERROR_DUPLICATE_MANAGER("api.error.people.duplicate-manager"),
	PEOPLE_ERROR_INVALID_HOLIDAY_YEAR("api.error.people.invalid.holiday.year"),
	PEOPLE_ERROR_VISA_DETAILS_NOT_FOUND("api.error.people.visa-details.not-found"),
	PEOPLE_ERROR_EMPLOYEE_EDUCATION_NOT_FOUND("api.error.people.employee-education.not-found"),
	PEOPLE_ERROR_EMPLOYEE_FAMILY_DETAILS_NOT_FOUND("api.error.people.employee-family-details.not-found"),
	PEOPLE_ERROR_MANAGER_NOT_FOUND("api.error.people.manager.not-found"),
	PEOPLE_ERROR_EMPLOYEE_ID_CANNOT_NULL("api.error.people.employee-id-cannot-null"),
	PEOPLE_ERROR_EMPLOYEE_ACCESS_DENIED("api.error.people.employee.access-denied"),
	PEOPLE_ERROR_MISSING_PREVIOUS_EMPLOYMENT_START_AND_END_DATES(
			"api.error.people.missing-previous-employment-start-and-end-dates"),
	PEOPLE_ERROR_PREVIOUS_EMPLOYMENT_START_DATE_INVALID("api.error.people.previous-employment-start-date-invalid"),
	PEOPLE_ERROR_PREVIOUS_EMPLOYMENT_END_DATE_INVALID("api.error.people.previous-employment-end-date-invalid"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_EEO_ENUM("api.error.people.invalid-value-for-eeo-enum"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_BLOOD_GROUP_ENUM("api.error.people.invalid-value-for-blood-group-enum"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_EMPLOYMENT_ALLOCATION_ENUM(
			"api.error.people.invalid-value-for-employment-allocation-enum"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_EMPLOYMENT_STATUS_ENUM("api.error.people.invalid-value-for-employment-status-enum"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_EMPLOYMENT_TYPE_ENUM("api.error.people.invalid-value-for-employment-type-enum"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_ETHNICITY_ENUM("api.error.people.invalid-value-for-ethnicity-enum"),
	PEOPLE_ERROR_INVALID_VALUE_FOR_MARITAL_STATUS_ENUM("api.error.people.invalid-value-for-marital-status-enum"),
	PEOPLE_ERROR_LOGIN_PENDING_EMPLOYEES_NOT_FOUND("api.error.people.login-pending-employees-not-found"),
	PEOPLE_ERROR_TEAM_MEMBER_IDS_CANNOT_NULL("api.error.people.team.member.ids.cannot-null"),
	PEOPLE_ERROR_USER_NOT_BELONGS_TO_SELECTED_TEAMS("api.error.people.user-not-belong-to-selected-teams"),
	PEOPLE_ERROR_DUPLICATE_EMPLOYEE_ID("api.error.people.duplicate-employee-id"),
	PEOPLE_ERROR_EXCEEDING_USER_UPLOAD("api.error.people.exceeding-user-upload"),
	PEOPLE_ERROR_EXCEEDING_MAX_CHARACTER_LIMIT("api.error.people.exceeding-max-character-limit"),;

	private final String messageKey;

}
