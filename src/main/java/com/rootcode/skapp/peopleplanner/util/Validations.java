package com.rootcode.skapp.peopleplanner.util;

import com.rootcode.skapp.common.constant.CommonMessageConstant;
import com.rootcode.skapp.common.exception.ModuleException;
import com.rootcode.skapp.common.exception.ValidationException;
import com.rootcode.skapp.common.type.Role;
import com.rootcode.skapp.common.util.DateTimeUtils;
import com.rootcode.skapp.peopleplanner.constant.PeopleConstants;
import com.rootcode.skapp.peopleplanner.model.EmployeeRole;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeDetailsDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeEducationDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmployeeFamilyDto;
import com.rootcode.skapp.peopleplanner.payload.request.EmploymentVisaDto;
import com.rootcode.skapp.peopleplanner.payload.request.ProbationPeriodDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static com.rootcode.skapp.common.util.Validation.ADDRESS_REGEX;
import static com.rootcode.skapp.common.util.Validation.COUNTRY_CODE_PATTERN;
import static com.rootcode.skapp.common.util.Validation.EMAIL_REGEX;
import static com.rootcode.skapp.common.util.Validation.NAME_REGEX;
import static com.rootcode.skapp.common.util.Validation.PHONE_NUMBER_PATTERN;
import static com.rootcode.skapp.common.util.Validation.VALID_IDENTIFICATION_NUMBER_REGEXP;
import static com.rootcode.skapp.common.util.Validation.VALID_NIN_NUMBER_REGEXP;

@Component
@RequiredArgsConstructor
public class Validations {

	public static boolean isEmployeeNameValid(String name) {
		if (!name.matches(NAME_REGEX)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMPLOYEE_NAME);
		}
		return true;
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		boolean isValid = false;
		if (phoneNumber.contains(" ")) {
			String countryCode = phoneNumber.substring(0, phoneNumber.indexOf(" "));
			String num = phoneNumber.substring(phoneNumber.indexOf(" ") + 1);
			if (Pattern.matches(COUNTRY_CODE_PATTERN, countryCode) && Pattern.matches(PHONE_NUMBER_PATTERN, num))
				isValid = true;
		}
		else {
			if (Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber))
				isValid = true;
		}
		return isValid;
	}

	public static boolean isValidIdentificationNo(String identificationNo) {
		return identificationNo.matches(VALID_IDENTIFICATION_NUMBER_REGEXP);
	}

	public static boolean validateTimeZone(String timeZone) {
		List<String> validIDs = List.of(TimeZone.getAvailableIDs());
		return validIDs.contains(timeZone);
	}

	public static boolean isManagerEligible(EmployeeRole role) {
		return (role.getPeopleRole().equals(Role.PEOPLE_MANAGER) || role.getPeopleRole().equals(Role.PEOPLE_ADMIN))
				|| (role.getAttendanceRole().equals(Role.ATTENDANCE_ADMIN)
						|| role.getAttendanceRole().equals(Role.ATTENDANCE_MANAGER))
				|| (role.getLeaveRole().equals(Role.LEAVE_ADMIN) || role.getLeaveRole().equals(Role.LEAVE_MANAGER))
				|| role.getIsSuperAdmin();
	}

	public static void validateVisaDates(List<EmploymentVisaDto> employeeVisas) {
		LocalDate currentDate = DateTimeUtils.getCurrentUtcDate();
		for (EmploymentVisaDto visa : employeeVisas) {
			if (visa.getIssuedDate() != null && visa.getIssuedDate().isAfter(currentDate)) {
				throw new ModuleException(CommonMessageConstant.COMMON_ERROR_VALIDATION_VISA_ISSUED_DATE);
			}
			if (visa.getExpirationDate() != null && visa.getExpirationDate().isBefore(currentDate)) {
				throw new ModuleException(CommonMessageConstant.COMMON_ERROR_VALIDATION_VISA_EXPIRATION_DATE);
			}
		}
	}

	public static void validateEmployeeDetails(EmployeeDetailsDto employeeDetailsDto) {
		if (employeeDetailsDto.getWorkEmail() == null || employeeDetailsDto.getWorkEmail().isEmpty())
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMAIL);
		else
			validateEmail(employeeDetailsDto.getWorkEmail());

		if (employeeDetailsDto.getFirstName() == null || employeeDetailsDto.getFirstName().isEmpty())
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_FIRST_NAME);
		else
			validateName(employeeDetailsDto.getFirstName());

		if (employeeDetailsDto.getLastName() == null || employeeDetailsDto.getLastName().isEmpty())
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_LAST_NAME);
		else
			validateName(employeeDetailsDto.getLastName());

		if (employeeDetailsDto.getMiddleName() != null && !employeeDetailsDto.getMiddleName().isEmpty())
			validateName(employeeDetailsDto.getMiddleName());

		if (employeeDetailsDto.getPersonalEmail() != null && !employeeDetailsDto.getPersonalEmail().isEmpty())
			validateEmail(employeeDetailsDto.getPersonalEmail());

		if (employeeDetailsDto.getPhone() != null && !employeeDetailsDto.getPhone().isEmpty())
			validatePhone(employeeDetailsDto.getPhone());

		if (employeeDetailsDto.getAddress() != null && !employeeDetailsDto.getAddress().isEmpty())
			validateAddress(employeeDetailsDto.getAddress());

		if (employeeDetailsDto.getAddressLine2() != null && !employeeDetailsDto.getAddressLine2().isEmpty())
			validateAddress(employeeDetailsDto.getAddressLine2());

		if (employeeDetailsDto.getIdentificationNo() != null && !employeeDetailsDto.getIdentificationNo().isEmpty())
			validateEmployeeIdentificationNo(employeeDetailsDto.getIdentificationNo());

		if (employeeDetailsDto.getJoinDate() != null)
			validateJoinedDate(employeeDetailsDto.getJoinDate());

		if (employeeDetailsDto.getJoinDate() != null && employeeDetailsDto.getEmployeePeriod() != null)
			validateStartAndJoinedDates(employeeDetailsDto.getJoinDate(),
					employeeDetailsDto.getEmployeePeriod().getStartDate());

		if (employeeDetailsDto.getEmployeePeriod() != null)
			validateEmployeePeriod(employeeDetailsDto.getEmployeePeriod());

		if (employeeDetailsDto.getEmployeePersonalInfo() != null
				&& employeeDetailsDto.getEmployeePersonalInfo().getBirthDate() != null)
			validateBirthDate(employeeDetailsDto.getEmployeePersonalInfo().getBirthDate());

		if (employeeDetailsDto.getEmployeePersonalInfo() != null
				&& employeeDetailsDto.getEmployeePersonalInfo().getNin().isEmpty())
			validateNIN(employeeDetailsDto.getEmployeePersonalInfo().getNin());

		if (employeeDetailsDto.getEmployeeEducations() != null && !employeeDetailsDto.getEmployeeEducations().isEmpty())
			validateEducationDetails(employeeDetailsDto.getEmployeeEducations());

		if (employeeDetailsDto.getEmployeeFamilies() != null && !employeeDetailsDto.getEmployeeFamilies().isEmpty())
			validateFamilyDetails(employeeDetailsDto.getEmployeeFamilies());

	}

	public static void validateEmail(String email) {
		if (email != null && !email.trim().matches(EMAIL_REGEX))
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMAIL);

		if (email != null && email.length() > PeopleConstants.MAX_EMAIL_LENGTH)
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMAIL_LENGTH,
					List.of(String.valueOf(PeopleConstants.MAX_EMAIL_LENGTH)));
	}

	public static void validateName(String name) {
		if (name != null && !name.trim().matches(NAME_REGEX))
			// Name can only have alphabets, accent characters, whitespace and diacritical
			// marks
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_NAME);

		if (name != null && name.length() > PeopleConstants.MAX_NAME_LENGTH)
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_NAME_LENGTH,
					List.of(String.valueOf(PeopleConstants.MAX_NAME_LENGTH)));

	}

	public static void validateAddress(String addressLine) {
		if (!addressLine.trim().matches(ADDRESS_REGEX))
			// Address can only have alphabets, accent characters, whitespace and
			// diacritical marks
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ADDRESS);

		if (addressLine.length() > PeopleConstants.MAX_ADDRESS_LENGTH)
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ADDRESS_LENGTH,
					List.of(String.valueOf(PeopleConstants.MAX_ADDRESS_LENGTH)));

	}

	public static void validatePhone(String phone) {
		if (!phone.trim().matches(PHONE_NUMBER_PATTERN))
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_PHONE_NUMBER);

		if (phone.length() > PeopleConstants.MAX_PHONE_LENGTH)
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_PHONE_NUMBER_LENGTH,
					List.of(String.valueOf(PeopleConstants.MAX_PHONE_LENGTH)));

	}

	public static void validateEmployeeIdentificationNo(String identificationNo) {
		if (!identificationNo.matches(VALID_IDENTIFICATION_NUMBER_REGEXP))
			// Identification number is not valid. Please provide a valid phone number
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_IDENTIFICATION_NUMBER);

		if (identificationNo.length() > PeopleConstants.MAX_ID_LENGTH)
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_IDENTIFICATION_NUMBER_LENGTH,
					List.of(String.valueOf(PeopleConstants.MAX_ID_LENGTH)));

	}

	public static void validateNIN(String nin) {
		if (!nin.trim().matches(VALID_NIN_NUMBER_REGEXP))
			// The National identification number is not valid. Please provide a valid
			// phone number
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_NIN);

		if (nin.length() > PeopleConstants.MAX_NIN_LENGTH)
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_NIN_LENGTH,
					List.of(String.valueOf(PeopleConstants.MAX_NIN_LENGTH)));

	}

	public static void validateJoinedDate(LocalDate joinDate) {
		if (joinDate.isAfter(LocalDate.now()))
			// Joined date should not be a future date
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_JOIN_DATE);
	}

	public static void validateEmployeePeriod(ProbationPeriodDto employeePeriod) {
		if (employeePeriod.getStartDate().isAfter(LocalDate.now()))
			// Start date should not be a future date
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_START_DATE);

		if (employeePeriod.getEndDate().isBefore(employeePeriod.getStartDate()))
			// End date should be after the start date
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_END_DATE);
	}

	public static void validateStartAndJoinedDates(LocalDate startDate, LocalDate joinDate) {
		if (startDate.isAfter(joinDate))
			// Start date should be before the joined date
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_START_DATE_JOIN_DATE);
	}

	public static void validateBirthDate(LocalDate birthDate) {
		if (!birthDate.isBefore(LocalDate.now()))
			// Birthdate should not be a future date
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_BIRTH_DATE);
	}

	public static void validateEducationDetails(List<EmployeeEducationDto> employeeEducations) {
		for (EmployeeEducationDto education : employeeEducations) {
			if (education.getInstitution().length() > PeopleConstants.MAX_EDUCATIONAL_DETAILS_LENGTH)
				// Institution cannot be empty
				throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_INSTITUTION);

			if (education.getDegree().length() > PeopleConstants.MAX_EDUCATIONAL_DETAILS_LENGTH)
				// Degree cannot be empty
				throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_DEGREE);

			if (education.getSpecialization().length() > PeopleConstants.MAX_EDUCATIONAL_DETAILS_LENGTH)
				// Specialization cannot be empty
				throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_SPECIALIZATION);

			if (education.getStartDate() != null && education.getEndDate() != null
					&& !education.getStartDate().isBefore(education.getEndDate()))
				throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_START_DATE_END_DATE);
		}
	}

	public static void validateFamilyDetails(List<EmployeeFamilyDto> employeeFamily) {
		for (EmployeeFamilyDto family : employeeFamily) {
			if (!family.getFirstName().isEmpty())
				validateName(family.getFirstName());

			if (!family.getLastName().isEmpty())
				validateName(family.getLastName());

			if (!family.getParentName().isEmpty())
				validateName(family.getParentName());

			if (family.getBirthDate() != null)
				validateBirthDate(family.getBirthDate());
		}
	}

}
