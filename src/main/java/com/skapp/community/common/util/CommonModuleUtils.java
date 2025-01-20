package com.skapp.community.common.util;

import com.skapp.community.leaveplanner.model.LeaveRequest;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.Holiday;
import com.skapp.community.peopleplanner.type.HolidayDuration;
import com.skapp.community.timeplanner.model.TimeConfig;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommonModuleUtils {

	public static final int MAX_USER_CHUNK_SIZE = 100;

	private CommonModuleUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Checks if a given date is a working day based on the time configurations.
	 * @param date The date to check.
	 * @param timeConfigs List of time configurations for working days.
	 * @return True if the date is a working day, false otherwise.
	 */
	public static boolean checkIfDayIsWorkingDay(LocalDate date, List<TimeConfig> timeConfigs) {
		DayOfWeek checkingDay = date.getDayOfWeek();

		if (timeConfigs.isEmpty()) {
			return true;
		}

		for (TimeConfig timeConfig : timeConfigs) {
			if (checkingDay.equals(timeConfig.getDay())) {
				return true;
			}
		}
		return false;
	}

	public static <T> List<List<T>> chunkData(List<T> dataList) {
		List<List<T>> chunkedList = new ArrayList<>();

		if (dataList != null && !dataList.isEmpty()) {
			int arrayLength = (int) Math.ceil((double) dataList.size() / MAX_USER_CHUNK_SIZE);
			for (int index = 0; index < arrayLength; index++) {
				int start = index * MAX_USER_CHUNK_SIZE;
				int end = Math.min(start + MAX_USER_CHUNK_SIZE, dataList.size());
				chunkedList.add(dataList.subList(start, end));
			}
		}

		return chunkedList;
	}

	public static <T> boolean isListNotNullAndNotEmpty(List<T> anyList) {
		return anyList != null && !anyList.isEmpty();
	}

	public static boolean isValidFloat(String floatStr) {
		if (floatStr != null && !floatStr.isBlank()) {
			try {
				Float.parseFloat(floatStr);
			}
			catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}

	public static float calculateHoursBetweenEpochMillis(long start, long end) {
		return (float) (end - start) / (1000 * 60 * 60);
	}

	public static DayOfWeek getDayOfWeek(LocalDate date) {
		return date.getDayOfWeek();
	}

	public static int addUpWorkingDaysForAllEmployee(List<Employee> employees, LocalDate startDate, LocalDate endDate,
			List<TimeConfig> timeConfigs, List<LocalDate> holidays) {
		int totalWorkingDays = 0;
		for (Employee employee : employees) {
			if (employee.getJoinDate() != null && startDate.isBefore(employee.getJoinDate())
					&& employee.getTerminationDate() != null && endDate.isAfter(employee.getTerminationDate())) {
				totalWorkingDays = totalWorkingDays + getWorkingDaysBetweenTwoDates(employee.getJoinDate(),
						employee.getTerminationDate(), timeConfigs, holidays);
			}
			else if (employee.getJoinDate() != null && startDate.isBefore(employee.getJoinDate())
					&& employee.getTerminationDate() == null) {
				totalWorkingDays = totalWorkingDays
						+ getWorkingDaysBetweenTwoDates(employee.getJoinDate(), endDate, timeConfigs, holidays);
			}
			else if (employee.getJoinDate() != null && startDate.isAfter(employee.getJoinDate())
					&& employee.getTerminationDate() == null) {
				totalWorkingDays = totalWorkingDays
						+ getWorkingDaysBetweenTwoDates(startDate, endDate, timeConfigs, holidays);
			}
			else if (employee.getJoinDate() != null && startDate.isAfter(employee.getJoinDate())
					&& employee.getTerminationDate() != null && endDate.isAfter(employee.getTerminationDate())) {
				totalWorkingDays = totalWorkingDays + getWorkingDaysBetweenTwoDates(startDate,
						employee.getTerminationDate(), timeConfigs, holidays);
			}
			else {
				totalWorkingDays = totalWorkingDays
						+ getWorkingDaysBetweenTwoDates(startDate, endDate, timeConfigs, holidays);
			}
		}
		return totalWorkingDays;
	}

	public static int getWorkingDaysBetweenTwoDates(LocalDate startDate, LocalDate endDate,
			List<TimeConfig> timeConfigs, List<LocalDate> holidays) {
		return getWorkingDaysBetweenTwoDates(startDate, endDate, timeConfigs, holidays, null, null);
	}

	public static int getWorkingDaysBetweenTwoDates(LocalDate startDate, LocalDate endDate,
			List<TimeConfig> timeConfigs, List<LocalDate> holidays, List<Holiday> holidayObjects,
			LeaveRequest leaveRequest) {
		// Ensure the start date is before the end date
		if (startDate.isAfter(endDate)) {
			LocalDate temp = startDate;
			startDate = endDate;
			endDate = temp;
		}

		int workDays = 0;

		LocalDate currentDate = startDate;

		while (!currentDate.isAfter(endDate)) {
			if (checkIfDayIsWorkingDay(currentDate, timeConfigs)
					&& checkIfDayIsNotAHoliday(leaveRequest, holidayObjects, holidays, currentDate)) {
				workDays++;
			}
			currentDate = currentDate.plusDays(1);
		}

		return workDays;
	}

	/**
	 * Checks if the given date is not a holiday based on holiday objects or a list of
	 * holidays.
	 * @param leaveRequest The leave request for context (used for checking half-day
	 * holidays).
	 * @param holidayObjects List of holiday objects, potentially containing half-day and
	 * full-day holidays.
	 * @param holidays List of holidays as LocalDate.
	 * @param date The date to check.
	 * @return True if the date is not a holiday, false otherwise.
	 */
	public static boolean checkIfDayIsNotAHoliday(LeaveRequest leaveRequest, List<Holiday> holidayObjects,
			List<LocalDate> holidays, LocalDate date) {
		boolean isNotHoliday = true;

		if (holidayObjects != null && !holidayObjects.isEmpty()) {
			List<Holiday> halfDays = holidayObjects.stream()

				.filter(holiday -> holiday.getHolidayDuration() == HolidayDuration.HALF_DAY_EVENING
						|| holiday.getHolidayDuration() == HolidayDuration.HALF_DAY_MORNING)
				.toList();

			List<Holiday> fullDays = holidayObjects.stream()
				.filter(holiday -> holiday.getHolidayDuration() == HolidayDuration.FULL_DAY)
				.toList();

			if (fullDays.stream().anyMatch(holiday -> holiday.getDate().equals(date))) {
				return false;
			}
			if (!halfDays.isEmpty()
					&& CommonModuleUtils.checkIfHalfDayLeaveAndHolidayOnSameDay(halfDays, leaveRequest, date)) {
				isNotHoliday = false;
			}
		}
		else if (holidays != null && !holidays.isEmpty() && date != null) {
			return !holidays.contains(date);
		}
		return isNotHoliday;
	}

	public static boolean checkIfHalfDayLeaveAndHolidayOnSameDay(List<Holiday> halfDays, LeaveRequest leaveRequest,
			LocalDate date) {

		return halfDays.stream()
			.filter(holiday -> holiday.getDate().equals(date))

			.anyMatch(
					holiday -> holiday.getHolidayDuration().toString().equals(leaveRequest.getLeaveState().toString()));
	}

	public static boolean validateStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
		int currentYear = DateTimeUtils.getCurrentYear();
		return startDate.getYear() < currentYear - 1 || endDate.getYear() < currentYear - 1
				|| startDate.isAfter(endDate);
	}

	public static List<Integer> getWorkingDaysIndex(List<TimeConfig> timeConfigs) {
		List<DayOfWeek> workingDays = timeConfigs.stream().map(TimeConfig::getDay).toList();
		List<Integer> workingDaysIndex = new ArrayList<>();
		for (DayOfWeek day : workingDays) {
			workingDaysIndex.add(day.getValue() - 1);
		}
		return workingDaysIndex;
	}

	public static String encodeToBase64(String password) {
		return Base64.getEncoder().encodeToString(password.getBytes());
	}

	public static String generateSecureRandomPassword() {
		String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		String numbers = RandomStringUtils.randomNumeric(2);
		String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
		String totalChars = RandomStringUtils.randomAlphanumeric(2);
		String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
			.concat(numbers)
			.concat(specialChar)
			.concat(totalChars);
		List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(pwdChars);
		return pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	}

}
