package com.skapp.community.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailBodyTemplates {

	// People Module Templates
	PEOPLE_MODULE_USER_INVITATION_V1("people-module-user-invitation"),
	PEOPLE_MODULE_USER_INVITATION_SSO("people-module-user-invitation-sso"),
	PEOPLE_MODULE_USER_TERMINATION_V1("people-module-user-termination"),

	// Holiday Templates
	PEOPLE_MODULE_NEW_HOLIDAY_DECLARED("people-module-new-holiday-declared-employee"),
	PEOPLE_MODULE_HOLIDAY_SINGLE_DAY_PENDING_LEAVE_REQUEST_CANCELED_EMPLOYEE(
			"people-module-holiday-single-day-pending-leave-request-canceled-employee"),
	PEOPLE_MODULE_HOLIDAY_MULTI_DAY_PENDING_LEAVE_REQUEST_UPDATED_EMPLOYEE(
			"people-module-holiday-multi-day-pending-leave-request-updated-employee"),
	PEOPLE_MODULE_HOLIDAY_SINGLE_DAY_APPROVED_LEAVE_REQUEST_REVOKED_EMPLOYEE(
			"people-module-holiday-single-day-approved-leave-request-revoked-employee"),
	PEOPLE_MODULE_HOLIDAY_MULTI_DAY_APPROVED_LEAVE_REQUEST_UPDATED_EMPLOYEE(
			"people-module-holiday-multi-day-approved-leave-request-updated-employee"),
	PEOPLE_MODULE_HOLIDAY_SINGLE_DAY_PENDING_LEAVE_REQUEST_CANCELED_MANAGER(
			"people-module-holiday-single-day-pending-leave-request-canceled-manager"),
	PEOPLE_MODULE_HOLIDAY_DAY_MULTI_DAY_PENDING_LEAVE_REQUEST_UPDATED_MANAGER(
			"people-module-holiday-multi-day-pending-leave-request-updated-manager"),
	PEOPLE_MODULE_HOLIDAY_DAY_SINGLE_DAY_APPROVED_LEAVE_REQUEST_REVOKED_MANAGER(
			"people-module-holiday-single-day-approved-leave-request-revoked-manager"),
	PEOPLE_MODULE_HOLIDAY_MULTI_DAY_APPROVED_LEAVE_REQUEST_UPDATED_MANAGER(
			"people-module-holiday-multi-day-approved-leave-request-updated-manager"),
	PEOPLE_MODULE_PASSWORD_RESET_REQUEST_MANAGER("people-module-password-reset-request-manager"),

	// Attendance Module Templates

	// Non-Working Day Templates
	ATTENDANCE_MODULE_NON_WORKING_DAY_SINGLE_DAY_PENDING_LEAVE_REQUEST_CANCELED_EMPLOYEE(
			"attendance-module-non-working-day-single-day-pending-leave-request-canceled-employee"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_MULTI_DAY_PENDING_LEAVE_REQUEST_CANCELED_EMPLOYEE(
			"attendance-module-non-working-day-multi-day-pending-leave-request-canceled-employee"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_SINGLE_DAY_APPROVED_LEAVE_REQUEST_REVOKED_EMPLOYEE(
			"attendance-module-non-working-day-single-day-approved-leave-request-revoked-employee"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_MULTI_DAY_APPROVED_LEAVE_REQUEST_REVOKED_EMPLOYEE(
			"attendance-module-non-working-day-multi-day-approved-leave-request-revoked-employee"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_SINGLE_DAY_PENDING_LEAVE_REQUEST_CANCELED_MANAGER(
			"attendance-module-non-working-day-single-day-pending-leave-request-canceled-manager"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_MULTI_DAY_PENDING_LEAVE_REQUEST_CANCELED_MANAGER(
			"attendance-module-non-working-day-multi-day-pending-leave-request-canceled-manager"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_SINGLE_DAY_APPROVED_LEAVE_REQUEST_REVOKED_MANAGER(
			"attendance-module-non-working-day-single-day-approved-leave-request-revoked-manager"),
	ATTENDANCE_MODULE_NON_WORKING_DAY_MULTI_DAY_APPROVED_LEAVE_REQUEST_REVOKED_MANAGER(
			"attendance-module-non-working-day-multi-day-approved-leave-request-revoked-manager"),

	// Time Entry Templates
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_SUBMITTED_EMPLOYEE("attendance-module-time-entry-request-submitted-employee"),
	ATTENDANCE_MODULE_RECEIVED_TIME_ENTRY_REQUEST_MANAGER("attendance-module-received-time-entry-request-manager"),
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_APPROVED_EMPLOYEE("attendance-module-time-entry-request-approved-employee"),
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_DECLINED_EMPLOYEE("attendance-module-time-entry-request-declined-employee"),
	ATTENDANCE_MODULE_PENDING_TIME_ENTRY_REQUEST_CANCELLED_EMPLOYEE(
			"attendance-module-pending-time-entry-request-cancelled-employee"),
	ATTENDANCE_MODULE_PENDING_TIME_ENTRY_REQUEST_CANCELLED_MANAGER(
			"attendance-module-pending-time-entry-request-cancelled-manager"),
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_AUTO_APPROVED_EMPLOYEE(
			"attendance-module-time-entry-request-auto-approved-employee"),
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_AUTO_APPROVED_MANAGER(
			"attendance-module-time-entry-request-auto-approved-manager"),
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_APPROVED_OTHER_MANAGER(
			"attendance-module-time-entry-request-approved-other-manager"),
	ATTENDANCE_MODULE_TIME_ENTRY_REQUEST_DECLINED_OTHER_MANAGER(
			"attendance-module-time-entry-request-declined-other-manager"),

	// Leave Module Templates
	LEAVE_MODULE_EMPLOYEE_APPLY_SINGLE_DAY_LEAVE("leave-module-employee-apply-single-day-leave"),
	LEAVE_MODULE_EMPLOYEE_APPLY_MULTIPLE_DAY_LEAVE("leave-module-employee-apply-multiple-day-leave"),
	LEAVE_MODULE_MANAGER_RECEIVED_SINGLE_DAY_LEAVE("leave-module-manager-received-single-day-leave"),
	LEAVE_MODULE_MANAGER_RECEIVED_MULTIPLE_DAY_LEAVE("leave-module-manager-received-multiple-day-leave"),
	LEAVE_MODULE_EMPLOYEE_CANCEL_SINGLE_DAY_LEAVE("leave-module-employee-cancel-single-day-leave"),
	LEAVE_MODULE_EMPLOYEE_CANCEL_MULTIPLE_DAY_LEAVE("leave-module-employee-cancel-multiple-day-leave"),
	LEAVE_MODULE_MANAGER_CANCEL_SINGLE_DAY_LEAVE("leave-module-manager-cancel-single-day-leave"),
	LEAVE_MODULE_MANAGER_CANCEL_MULTIPLE_DAY_LEAVE("leave-module-manager-cancel-multiple-day-leave"),
	LEAVE_MODULE_EMPLOYEE_APPROVED_SINGLE_DAY_LEAVE("leave-module-employee-approved-single-day-leave"),
	LEAVE_MODULE_EMPLOYEE_APPROVED_MULTI_DAY_LEAVE("leave-module-employee-approved-multi-day-leave"),
	LEAVE_MODULE_EMPLOYEE_REVOKED_SINGLE_DAY_LEAVE("leave-module-employee-revoked-single-day-leave"),
	LEAVE_MODULE_EMPLOYEE_REVOKED_MULTI_DAY_LEAVE("leave-module-employee-revoked-multi-day-leave"),
	LEAVE_MODULE_EMPLOYEE_DECLINED_SINGLE_DAY_LEAVE("leave-module-employee-declined-single-day-leave"),
	LEAVE_MODULE_EMPLOYEE_DECLINED_MULTI_DAY_LEAVE("leave-module-employee-declined-multi-day-leave"),
	LEAVE_MODULE_EMPLOYEE_AUTO_APPROVED_SINGLE_DAY_LEAVE("leave-module-employee-auto-approved-single-day-leave"),
	LEAVE_MODULE_EMPLOYEE_AUTO_APPROVED_MULTI_DAY_LEAVE("leave-module-employee-auto-approved-multi-day-leave"),
	LEAVE_MODULE_EMPLOYEE_LEAVE_CARRY_FORWARD("leave-module-employee-leave-carry-forward"),
	LEAVE_MODULE_EMPLOYEE_CUSTOM_ALLOCATION("leave-module-employee-custom-allocation"),
	LEAVE_MODULE_MANAGER_APPROVED_SINGLE_DAY_LEAVE("leave-module-manager-approved-single-day-leave"),
	LEAVE_MODULE_MANAGER_APPROVED_MULTI_DAY_LEAVE("leave-module-manager-approved-multi-day-leave"),
	LEAVE_MODULE_MANAGER_REVOKED_SINGLE_DAY_LEAVE("leave-module-manager-revoked-single-day-leave"),
	LEAVE_MODULE_MANAGER_REVOKED_MULTI_DAY_LEAVE("leave-module-manager-revoked-multi-day-leave"),
	LEAVE_MODULE_MANAGER_DECLINED_SINGLE_DAY_LEAVE("leave-module-manager-declined-single-day-leave"),
	LEAVE_MODULE_MANAGER_DECLINED_MULTI_DAY_LEAVE("leave-module-manager-declined-multi-day-leave"),
	LEAVE_MODULE_MANAGER_NUDGE_SINGLE_DAY_LEAVE("leave-module-manager-nudge-single-day-leave"),
	LEAVE_MODULE_MANAGER_NUDGE_MULTI_DAY_LEAVE("leave-module-manager-nudge-multi-day-leave"),
	LEAVE_MODULE_MANAGER_AUTO_APPROVED_SINGLE_DAY_LEAVE("leave-module-manager-auto-approved-single-day-leave"),
	LEAVE_MODULE_MANAGER_AUTO_APPROVED_MULTI_DAY_LEAVE("leave-module-manager-auto-approved-multi-day-leave"),

	// Common Module Templates
	COMMON_MODULE_EMAIL_VERIFY("common-module-email-verify"),
	COMMON_MODULE_PASSWORD_RESET_OTP("common-module-password-reset-otp"),
	COMMON_MODULE_SSO_CREATION_TENANT_URL("common-module-sso-creation-tenant-url"),
	COMMON_MODULE_CREDENTIAL_BASED_CREATION_TENANT_URL("common-module-credential-based-creation-tenant-url"),

	// E-Signature Module Templates esignature-module-document-viewer-email
	ESIGNATURE_MODULE_ENVELOPE_CC_EMAIL("esignature-module-document-viewer-email"),
	ESIGNATURE_MODULE_ENVELOPE_SIGNER_EMAIL("esignature-module-document-signer-email"),
	ESIGNATURE_MODULE_ENVELOPE_VOIDED_RECIEVER_EMAIL("esignature-module-document-voided-reciever"),
	ESIGNATURE_MODULE_ENVELOPE_VOIDED_SENDER_EMAIL("esignature-module-document-voided-sender"),
	ESIGNATURE_MODULE_ENVELOPE_DECLINED_RECIEVER_EMAIL("esignature-module-document-declined-reciever"),
	ESIGNATURE_MODULE_ENVELOPE_DECLINED_SENDER_EMAIL("esignature-module-document-declined-sender"),

	// Payment Templates for stripe
	PAYMENT_STRIPE_PAYMENT_WAS_UNSUCCESSFUL_TRIAL_END_DATE("payment-stripe-payment-was-unsuccessful-trial-end-date"),
	PAYMENT_STRIPE_PAYMENT_WAS_UNSUCCESSFUL_AFTER_3DAYS_AND_5DAYS(
			"payment-stripe-payment-was-unsuccessful-after-3days-and-5days"),
	PAYMENT_STRIPE_PAYMENT_WAS_UNSUCCESSFUL_AFTER_7DAYS("payment-stripe-payment-was-unsuccessful-after-7days"),
	PAYMENT_STRIPE_WELCOME_TO_SKAPP_PRO_FREE_TRIAL("payment-stripe-welcome-to-skapp-pro-free-trial"),
	PAYMENT_STRIPE_FREE_TRIAL_EXPIRES_IN_3DAYS("payment-stripe-free-trial-expires-in-3days"),
	PAYMENT_STRIPE_CONGRATULATIONS_ON_UPGRADING_TO_SKAPP_PRO(
			"payment-stripe-congratulations-on-upgrading-to-skapp-pro");

	private final String templateId;

}
