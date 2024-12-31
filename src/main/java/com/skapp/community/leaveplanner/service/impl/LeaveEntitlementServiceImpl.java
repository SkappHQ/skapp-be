package com.skapp.community.leaveplanner.service.impl;

import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.EntityNotFoundException;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.model.User;
import com.skapp.community.common.payload.response.BulkResponseDto;
import com.skapp.community.common.payload.response.BulkStatusSummaryDto;
import com.skapp.community.common.payload.response.ErrorLogDto;
import com.skapp.community.common.payload.response.PageDto;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.repository.UserDao;
import com.skapp.community.common.service.BulkContextService;
import com.skapp.community.common.service.UserService;
import com.skapp.community.common.type.BulkItemStatus;
import com.skapp.community.common.util.CommonModuleUtils;
import com.skapp.community.common.util.DateTimeUtils;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.common.util.transformer.PageTransformer;
import com.skapp.community.leaveplanner.constant.LeaveMessageConstant;
import com.skapp.community.leaveplanner.constant.LeaveModuleConstant;
import com.skapp.community.leaveplanner.mapper.LeaveMapper;
import com.skapp.community.leaveplanner.model.CarryForwardInfo;
import com.skapp.community.leaveplanner.model.LeaveEntitlement;
import com.skapp.community.leaveplanner.model.LeaveType;
import com.skapp.community.leaveplanner.payload.BulkLeaveEntitlementDto;
import com.skapp.community.leaveplanner.payload.CarryForwardDetailsResponseDto;
import com.skapp.community.leaveplanner.payload.CarryForwardEntitlementDto;
import com.skapp.community.leaveplanner.payload.CarryForwardLeaveTypesFilterDto;
import com.skapp.community.leaveplanner.payload.CustomEntitlementDto;
import com.skapp.community.leaveplanner.payload.CustomEntitlementsFilterDto;
import com.skapp.community.leaveplanner.payload.CustomLeaveEntitlementDto;
import com.skapp.community.leaveplanner.payload.CustomLeaveEntitlementPatchRequestDto;
import com.skapp.community.leaveplanner.payload.CustomLeaveEntitlementsFilterDto;
import com.skapp.community.leaveplanner.payload.EntitlementDetailsDto;
import com.skapp.community.leaveplanner.payload.EntitlementDto;
import com.skapp.community.leaveplanner.payload.LeaveBulkSkippedCountDto;
import com.skapp.community.leaveplanner.payload.LeaveCycleDetailsDto;
import com.skapp.community.leaveplanner.payload.LeaveEntitlementCarryForwardResponseDto;
import com.skapp.community.leaveplanner.payload.LeaveEntitlementPatchRequestDto;
import com.skapp.community.leaveplanner.payload.LeaveEntitlementResponseDto;
import com.skapp.community.leaveplanner.payload.LeaveEntitlementsDto;
import com.skapp.community.leaveplanner.payload.LeaveEntitlementsFilterDto;
import com.skapp.community.leaveplanner.payload.SummarizedCustomLeaveEntitlementDto;
import com.skapp.community.leaveplanner.payload.response.EntitlementBasicDetailsDto;
import com.skapp.community.leaveplanner.payload.response.LeaveTypeBasicDetailsResponseDto;
import com.skapp.community.leaveplanner.repository.CarryForwardInfoDao;
import com.skapp.community.leaveplanner.repository.LeaveEntitlementDao;
import com.skapp.community.leaveplanner.repository.LeaveTypeDao;
import com.skapp.community.leaveplanner.service.LeaveCycleService;
import com.skapp.community.leaveplanner.service.LeaveEmailService;
import com.skapp.community.leaveplanner.service.LeaveEntitlementService;
import com.skapp.community.leaveplanner.service.LeaveNotificationService;
import com.skapp.community.leaveplanner.type.CustomLeaveEntitlementSort;
import com.skapp.community.leaveplanner.type.LeaveDuration;
import com.skapp.community.leaveplanner.util.LeaveModuleUtil;
import com.skapp.community.peopleplanner.constant.PeopleConstants;
import com.skapp.community.peopleplanner.constant.PeopleMessageConstant;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeTimeline;
import com.skapp.community.peopleplanner.payload.request.EmployeeBasicDetailsResponseDto;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.EmployeeTimelineDao;
import com.skapp.community.peopleplanner.type.EmployeeTimelineType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveEntitlementServiceImpl implements LeaveEntitlementService {

	@NonNull
	private final MessageUtil messageUtil;

	@NonNull
	private final EmployeeDao employeeDao;

	@NonNull
	private final LeaveCycleService leaveCycleService;

	@NonNull
	private final LeaveTypeDao leaveTypeDao;

	@NonNull
	private final LeaveEntitlementDao leaveEntitlementDao;

	@NonNull
	private final EmployeeTimelineDao employeeTimelineDao;

	@NonNull
	private final LeaveMapper leaveMapper;

	@NonNull
	private final PeopleMapper peopleMapper;

	@NonNull
	private final CarryForwardInfoDao carryForwardInfoDao;

	@NonNull
	private final PageTransformer pageTransformer;

	@NonNull
	private final PlatformTransactionManager transactionManager;

	@NonNull
	private final UserDao userDao;

	@NonNull
	private final UserService userService;

	@NonNull
	private final LeaveEmailService leaveEmailService;

	@NonNull
	private final LeaveNotificationService leaveNotificationService;

	@NonNull
	private final BulkContextService bulkContextService;

	@Override
	public String processLeaveEntitlements(LeaveEntitlementsDto leaveEntitlementsDto) {
		log.info("processLeaveEntitlements: execution started");

		LeaveCycleDetailsDto leaveCycleDetails = leaveCycleService.getLeaveCycleConfigs();

		int leaveCycleEndYear = LeaveModuleUtil.getLeaveCycleEndYear(leaveCycleDetails.getStartMonth(),
				leaveCycleDetails.getStartDate());

		LocalDate leaveCycleStartingDate = DateTimeUtils
			.getUtcLocalDate(
					leaveCycleDetails.getStartMonth() == 1 && leaveCycleDetails.getStartDate() == 1 ? leaveCycleEndYear
							: leaveCycleEndYear - 1,
					leaveCycleDetails.getStartMonth(), leaveCycleDetails.getStartDate());

		LocalDate leaveCycleEndingDate = DateTimeUtils.getUtcLocalDate(
				leaveCycleDetails.getStartMonth() == 1 && leaveCycleDetails.getStartDate() == 1 ? leaveCycleEndYear
						: leaveCycleEndYear - 1,
				leaveCycleDetails.getEndMonth(), leaveCycleDetails.getEndDate());

		Optional<Employee> optionalEmployee = employeeDao.findById(leaveEntitlementsDto.getEmployeeId());
		if (optionalEmployee.isEmpty()) {
			throw new EntityNotFoundException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		List<Long> leaveTypeIds = leaveEntitlementsDto.getEntitlementList()
			.stream()
			.map(EntitlementDto::getLeaveTypeId)
			.toList();
		List<LeaveType> leaveTypes = leaveTypeDao.findAllById(leaveTypeIds);
		List<EntitlementDto> existingEntitlements = leaveEntitlementsDto.getEntitlementList()
			.stream()
			.filter(entitlement -> leaveTypes.stream()
				.anyMatch(leaveType -> leaveType.getTypeId().equals(entitlement.getLeaveTypeId())))
			.toList();

		List<LeaveEntitlement> leaveEntitlements = new ArrayList<>();
		List<EmployeeTimeline> employeeTimelines = new ArrayList<>();

		for (EntitlementDto entitlementDto : existingEntitlements) {
			LeaveType leaveType = leaveTypes.stream()
				.filter(type -> type.getTypeId().equals(entitlementDto.getLeaveTypeId()))
				.findFirst()
				.orElse(null);

			LeaveEntitlement leaveEntitlement = new LeaveEntitlement();
			leaveEntitlement.setEmployee(optionalEmployee.get());
			leaveEntitlement.setManual(false);
			leaveEntitlement.setLeaveType(leaveType);
			leaveEntitlement.setTotalDaysAllocated(
					(entitlementDto.getTotalDaysAllocated() == null ? 0F : entitlementDto.getTotalDaysAllocated()));
			leaveEntitlement.setTotalDaysUsed(0F);
			leaveEntitlement.setValidFrom(leaveCycleStartingDate);
			leaveEntitlement.setValidTo(leaveCycleEndingDate);
			leaveEntitlements.add(leaveEntitlement);
			employeeTimelines.add(getEmployeeTimeline(optionalEmployee.get(), EmployeeTimelineType.ENTITLEMENT_ADDED,
					PeopleConstants.TITLE_ENTITLEMENT_ADDED, null,
					leaveEntitlement.getLeaveType().getName() + " " + leaveEntitlement.getTotalDaysAllocated()));
		}

		String message;
		String leaveTypesNameString = "";

		if (!existingEntitlements.isEmpty()) {
			leaveTypesNameString = leaveTypes.stream()
				.map(LeaveType::getName)
				.toList()
				.stream()
				.map(Object::toString)
				.reduce((leaveType1, leaveType2) -> leaveType1 + ", " + leaveType2)
				.orElse("");
		}

		if (existingEntitlements.size() == leaveEntitlementsDto.getEntitlementList().size()) {
			message = "Successfully added entitlements for leave types " + leaveTypesNameString + " for user with id "
					+ leaveEntitlementsDto.getEmployeeId();
		}
		else {
			List<EntitlementDto> notAddedEntitlements = leaveEntitlementsDto.getEntitlementList()
				.stream()
				.filter(entitlement -> leaveTypes.stream()
					.noneMatch(leaveType -> leaveType.getTypeId().equals(entitlement.getLeaveTypeId())))
				.toList();
			String entitlementIds = notAddedEntitlements.stream()
				.map(EntitlementDto::getLeaveTypeId)
				.toList()
				.stream()
				.map(Object::toString)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
			if (!existingEntitlements.isEmpty())
				message = "Successfully added entitlements for leave types " + leaveTypesNameString
						+ ". Leave types with ids " + entitlementIds + " are not found";
			else
				message = "Leave types with ids " + entitlementIds + " are not found";

		}

		leaveEntitlementDao.saveAll(leaveEntitlements);
		employeeTimelineDao.saveAll(employeeTimelines);

		log.info("processLeaveEntitlements: execution ended");
		return message;
	}

	@Override
	@Transactional
	public void updateLeaveEntitlements(Long entitlementId,
			LeaveEntitlementPatchRequestDto leaveEntitlementPatchRequestDto) {
		log.info("updateLeaveEntitlements: execution started");

		Optional<LeaveEntitlement> optionalLeaveEntitlement = leaveEntitlementDao.findById(entitlementId);
		if (optionalLeaveEntitlement.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_NOT_FOUND);
		}

		LeaveEntitlement leaveEntitlement = optionalLeaveEntitlement.get();

		if (!leaveEntitlement.isActive()) {
			log.warn("Entitlement {} is inactive, no updates performed", entitlementId);
			return;
		}

		if (Boolean.TRUE.equals(leaveEntitlementPatchRequestDto.getIsDeactivate())) {
			disableLeaveEntitlement(leaveEntitlement);
		}
		else {
			editLeaveEntitlement(leaveEntitlementPatchRequestDto, leaveEntitlement);
		}
		leaveEntitlement = leaveEntitlementDao.save(leaveEntitlement);

		String oldHistoryRecord = leaveEntitlement.getLeaveType().getName() + " "
				+ leaveEntitlement.getTotalDaysAllocated();
		String historyTitle = optionalLeaveEntitlement.get().isManual()
				? PeopleConstants.TITLE_CUSTOM_ALLOCATION_UPDATED : PeopleConstants.TITLE_ENTITLEMENT_UPDATED;
		String newHistoryRecord = leaveEntitlement.getLeaveType().getName() + " "
				+ (leaveEntitlement.getTotalDaysAllocated() == null ? 0.0 : leaveEntitlement.getTotalDaysAllocated());

		saveChangesEmployeeHistory(oldHistoryRecord, newHistoryRecord, leaveEntitlement, historyTitle);

		log.info("updateLeaveEntitlements: execution ended");
	}

	@Override
	@Transactional
	public void updateCustomLeaveEntitlements(Long entitlementId,
			CustomLeaveEntitlementPatchRequestDto customLeaveEntitlementPatchRequestDto) {
		log.info("updateCustomLeaveEntitlements: execution started");

		Optional<LeaveEntitlement> optionalCustomLeaveEntitlement = leaveEntitlementDao.findById(entitlementId);
		if (optionalCustomLeaveEntitlement.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_NOT_FOUND);
		}

		LeaveEntitlement customLeaveEntitlement = optionalCustomLeaveEntitlement.get();

		String oldHistoryRecord = customLeaveEntitlement.getLeaveType().getName() + " "
				+ customLeaveEntitlement.getTotalDaysAllocated();

		if (!customLeaveEntitlement.isActive()) {
			log.warn("Custom entitlement {} is inactive, no updates performed", entitlementId);
			return;
		}

		editCustomLeaveEntitlement(customLeaveEntitlementPatchRequestDto, customLeaveEntitlement);

		leaveEntitlementDao.save(customLeaveEntitlement);

		String newHistoryRecord = customLeaveEntitlement.getLeaveType().getName() + " "
				+ (customLeaveEntitlement.getTotalDaysAllocated() == null ? 0.0
						: customLeaveEntitlement.getTotalDaysAllocated());

		addNewEmployeeLeaveEntitlementTimelineRecord(null, EmployeeTimelineType.CUSTOM_ALLOCATION,
				PeopleConstants.TITLE_CUSTOM_ALLOCATION_UPDATED, oldHistoryRecord, newHistoryRecord);

		log.info("updateCustomLeaveEntitlements: execution ended");
	}

	@Override
	public ResponseEntityDto deleteCustomLeaveEntitlements(Long id) {
		log.info("deleteCustomLeaveEntitlements: execution started");

		Optional<LeaveEntitlement> optionalLeaveEntitlement = leaveEntitlementDao.findById(id);
		if (optionalLeaveEntitlement.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_NOT_FOUND);
		}

		LeaveEntitlement leaveEntitlement = optionalLeaveEntitlement.get();
		if (leaveEntitlement.isActive() && Boolean.TRUE.equals(leaveEntitlement.isManual())) {
			if (leaveEntitlement.getTotalDaysUsed() > 0) {
				throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_ENTITLEMENT_IN_USE_CANT_DELETED);
			}
			leaveEntitlementDao.delete(leaveEntitlement);
		}

		log.info("deleteCustomLeaveEntitlements: execution ended");
		return new ResponseEntityDto(false, "Leave entitlement deleted successfully");
	}

	private void disableLeaveEntitlement(LeaveEntitlement leaveEntitlement) {
		leaveEntitlement.setActive(false);
		leaveEntitlementDao.save(leaveEntitlement);
		log.info("deleteTeamById: execution ended successfully for leave entitlement: {}",
				leaveEntitlement.getEntitlementId());
	}

	private void editLeaveEntitlement(LeaveEntitlementPatchRequestDto leaveEntitlementPatchRequestDto,
			LeaveEntitlement leaveEntitlement) {
		if (leaveEntitlementPatchRequestDto.getDays() != null) {
			if (leaveEntitlementPatchRequestDto.getDays() <= 0) {
				throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_NUMBER_OF_DAYS_NOT_VALID);
			}
			if (leaveEntitlementPatchRequestDto.getDays() % 0.5 != 0) {
				throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_INVALID_DAYS_OFF_ALLOCATION);
			}

			leaveEntitlement.setTotalDaysAllocated(leaveEntitlementPatchRequestDto.getDays());
		}

		if (leaveEntitlementPatchRequestDto.getLeaveTypeId() != null) {
			setLeaveEntitlementLeaveType(leaveEntitlementPatchRequestDto.getLeaveTypeId(), leaveEntitlement);
		}

		if (leaveEntitlementPatchRequestDto.getValidFrom() != null) {
			leaveEntitlement.setValidFrom(leaveEntitlementPatchRequestDto.getValidFrom());
		}

		leaveEntitlement.setValidTo(leaveEntitlementPatchRequestDto.getValidTo() != null
				? leaveEntitlementPatchRequestDto.getValidTo()
				: DateTimeUtils.getUtcLocalDate(DateTimeUtils.getYear(leaveEntitlement.getValidFrom()), 11, 31));

		if (leaveEntitlementPatchRequestDto.getReason() != null) {
			leaveEntitlement.setReason(leaveEntitlementPatchRequestDto.getReason());
		}

		leaveEntitlement.setOverride(leaveEntitlementPatchRequestDto.getIsOverride());
	}

	private void editCustomLeaveEntitlement(CustomLeaveEntitlementPatchRequestDto leaveEntitlementPatchRequestDto,
			LeaveEntitlement leaveEntitlement) {
		if (leaveEntitlementPatchRequestDto.getNumberOfDaysOff() != null) {
			if (leaveEntitlementPatchRequestDto.getNumberOfDaysOff() <= 0) {
				throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_NUMBER_OF_DAYS_NOT_VALID);
			}
			if (leaveEntitlementPatchRequestDto.getNumberOfDaysOff() % 0.5 != 0) {
				throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_INVALID_DAYS_OFF_ALLOCATION);
			}

			leaveEntitlement.setTotalDaysAllocated(leaveEntitlementPatchRequestDto.getNumberOfDaysOff());
		}

		if (leaveEntitlementPatchRequestDto.getTypeId() != null) {
			setLeaveEntitlementLeaveType(leaveEntitlementPatchRequestDto.getTypeId(), leaveEntitlement);
		}

		if (leaveEntitlementPatchRequestDto.getValidFromDate() != null) {
			leaveEntitlement.setValidFrom(leaveEntitlementPatchRequestDto.getValidFromDate());
		}

		leaveEntitlement.setValidTo(leaveEntitlementPatchRequestDto.getValidToDate() != null
				? leaveEntitlementPatchRequestDto.getValidToDate()
				: DateTimeUtils.getUtcLocalDate(DateTimeUtils.getYear(leaveEntitlement.getValidFrom()), 11, 31));
	}

	private void setLeaveEntitlementLeaveType(long leaveTypeId, LeaveEntitlement leaveEntitlement) {
		Optional<LeaveType> leaveType = leaveTypeDao.findById(leaveTypeId);
		if (leaveType.isPresent()) {
			leaveEntitlement.setLeaveType(leaveType.get());
		}
		else {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_TYPE_NOT_FOUND);
		}
	}

	private void saveChangesEmployeeHistory(String oldHistoryLog, String newHistoryLog,
			LeaveEntitlement leaveEntitlement, String title) {
		if (!oldHistoryLog.equals(newHistoryLog)) {
			addNewEmployeeLeaveEntitlementTimelineRecord(leaveEntitlement.getEmployee(),
					EmployeeTimelineType.ENTITLEMENT_CHANGED, title, oldHistoryLog, newHistoryLog);
		}
	}

	@Override
	public void addNewEmployeeLeaveEntitlementTimelineRecord(Employee employee, EmployeeTimelineType timelineType,
			String title, String previousValue, String newValue) {
		log.info("addNewEmployeeLeaveEntitlementTimelineRecord: execution started");

		EmployeeTimeline timeline = getEmployeeTimeline(employee, timelineType, title, previousValue, newValue);

		employeeTimelineDao.save(timeline);
		log.info("addNewEmployeeLeaveEntitlementTimelineRecord: execution ended");
	}

	@Override
	@Transactional
	public ResponseEntityDto deleteDefaultEntitlements(Long employeeId) {
		log.info("deleteDefaultEntitlements: execution started");

		Optional<Employee> employeeOpt = employeeDao.findById(employeeId);
		if (employeeOpt.isEmpty()) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		LeaveEntitlementsFilterDto leaveEntitlementsFilterDto = new LeaveEntitlementsFilterDto();
		leaveEntitlementsFilterDto.setIsManual(false);
		List<LeaveEntitlement> leaveEntitlements = leaveEntitlementDao.findEntitlementsByEmployeeIdAndYear(employeeId,
				leaveEntitlementsFilterDto);

		if (leaveEntitlements.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_NOT_FOUND);
		}

		leaveEntitlements.forEach(e -> {
			e.setActive(false);
			addNewEmployeeLeaveEntitlementTimelineRecord(employeeOpt.get(), EmployeeTimelineType.ENTITLEMENT_CHANGED,
					PeopleConstants.TITLE_DEFAULT_ENTITLEMENT_DELETED, null, e.getLeaveType().getName());
		});
		leaveEntitlementDao.saveAll(leaveEntitlements);

		log.info("deleteDefaultEntitlements: execution ended successfully for deleting Default Leave Entitlements");
		List<LeaveEntitlementResponseDto> leaveEntitlementResponseDtos = leaveMapper
			.leaveEntitlementsToEntitlementResponseDtoList(leaveEntitlements);
		return new ResponseEntityDto(false, leaveEntitlementResponseDtos);
	}

	@Override
	public ResponseEntityDto createCustomEntitlementForEmployee(CustomLeaveEntitlementDto customLeaveEntitlementDto) {
		log.info("createCustomEntitlementForEmployee: execution started");

		Optional<Employee> employeeOpt = employeeDao.findById(customLeaveEntitlementDto.getEmployeeId());
		if (employeeOpt.isEmpty()) {
			throw new EntityNotFoundException(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		}

		Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findById(customLeaveEntitlementDto.getTypeId());
		if (leaveTypeOpt.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_TYPE_NOT_FOUND);
		}

		if (customLeaveEntitlementDto.getNumberOfDaysOff() <= 0) {
			throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_NUMBER_OF_DAYS_NOT_VALID);
		}

		if (customLeaveEntitlementDto.getNumberOfDaysOff() % 0.5 != 0) {
			throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_INVALID_DAYS_OFF_ALLOCATION);
		}

		LeaveEntitlement leaveEntitlement = new LeaveEntitlement();
		if (!leaveTypeOpt.get().getIsActive()) {
			leaveEntitlement.setActive(false);
		}

		leaveEntitlement.setEmployee(employeeOpt.get());
		leaveEntitlement.setManual(true);
		leaveEntitlement.setLeaveType(leaveTypeOpt.get());
		leaveEntitlement.setTotalDaysAllocated(customLeaveEntitlementDto.getNumberOfDaysOff());
		leaveEntitlement.setTotalDaysUsed(0F);

		// If the valid_from and valid_to dates are not given, the default value is set
		// from the current date, till the end of the current year.
		if (customLeaveEntitlementDto.getValidFromDate() == null) {
			customLeaveEntitlementDto.setValidFromDate(DateTimeUtils.getCurrentUtcDate());
		}
		if (customLeaveEntitlementDto.getValidToDate() == null) {
			LeaveCycleDetailsDto leaveCycleDetails = leaveCycleService.getLeaveCycleConfigs();
			int year = LeaveModuleUtil.getLeaveCycleEndYear(leaveCycleDetails.getStartMonth() - 1,
					leaveCycleDetails.getStartDate());
			LocalDate endOfYear = DateTimeUtils.getUtcLocalDate(year, leaveCycleDetails.getEndMonth() - 1,
					leaveCycleDetails.getEndDate());
			customLeaveEntitlementDto.setValidToDate(endOfYear);
		}
		leaveEntitlement.setValidFrom(customLeaveEntitlementDto.getValidFromDate());
		leaveEntitlement.setValidTo(customLeaveEntitlementDto.getValidToDate());

		leaveEntitlementDao.save(leaveEntitlement);

		leaveEmailService.sendCustomAllocationEmployeeEmail(leaveEntitlement);
		leaveNotificationService.sendCustomAllocationEmployeeNotification(leaveEntitlement);

		addNewEmployeeLeaveEntitlementTimelineRecord(employeeOpt.get(), EmployeeTimelineType.CUSTOM_ALLOCATION,
				PeopleConstants.TITLE_CUSTOM_ALLOCATION, null,
				leaveEntitlement.getLeaveType().getName() + " " + leaveEntitlement.getTotalDaysAllocated());

		LeaveEntitlementResponseDto leaveEntitlementResponseDto = leaveMapper
			.leaveEntitlementToEntitlementResponseDto(leaveEntitlement);
		log.info("createCustomEntitlementForEmployee: execution ended");
		return new ResponseEntityDto(false, leaveEntitlementResponseDto);
	}

	@Override
	public ResponseEntityDto addLeaveEntitlements(LeaveEntitlementsDto leaveEntitlementsDto) {
		log.info("addLeaveEntitlements: execution started");

		String processLeaveEntitlementsResponse = processLeaveEntitlements(leaveEntitlementsDto);

		log.info("addLeaveEntitlements: execution ended");
		return new ResponseEntityDto(processLeaveEntitlementsResponse, false);
	}

	@Override
	public ResponseEntityDto getLeaveEntitlementById(Long id) {
		log.info("getLeaveEntitlementById: execution started");

		List<LeaveEntitlement> leaveEntitlement = leaveEntitlementDao.findByIdAndIsActive(id);
		if (leaveEntitlement.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_NOT_FOUND);
		}

		LeaveEntitlementResponseDto leaveEntitlementResponseDto = leaveMapper
			.leaveEntitlementToEntitlementResponseDto(leaveEntitlement.getFirst());

		log.info("getLeaveEntitlementById: execution ended");
		return new ResponseEntityDto(false, leaveEntitlementResponseDto);
	}

	@Override
	public ResponseEntityDto getCustomLeaveEntitlementById(Long id) {
		log.info("getCustomLeaveEntitlementById: execution started");

		List<LeaveEntitlement> leaveEntitlement = leaveEntitlementDao.findCustomLeavesById(id);
		if (leaveEntitlement.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_NOT_FOUND);
		}

		LeaveEntitlementResponseDto leaveEntitlementResponseDto = leaveMapper
			.leaveEntitlementToEntitlementResponseDto(leaveEntitlement.getFirst());
		log.info("getCustomLeaveEntitlementById: execution ended successfully for getting Leave Entitlement");
		return new ResponseEntityDto(false, leaveEntitlementResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto forceCarryForwardEntitlements(List<Long> leaveTypeIds, Integer cycleStartYear) {
		log.info("forceCarryForwardEntitlements: execution started");

		if (!leaveCycleService.isInNextCycle(cycleStartYear)) {
			throw new ModuleException(LeaveMessageConstant.LEAVE_ERROR_CARRY_FORWARD_YEAR_NOT_VALID);
		}

		LeaveCycleDetailsDto leaveCycleDetail = leaveCycleService.getLeaveCycleConfigs();

		LocalDate leaveCycleStartDate = DateTimeUtils.getUtcLocalDate(DateTimeUtils.getCurrentYear(),
				leaveCycleDetail.getStartMonth(), leaveCycleDetail.getStartDate());
		LocalDate leaveCycleEndDate = DateTimeUtils.calculateEndDateAfterYears(leaveCycleStartDate, 1);

		List<LeaveType> leaveTypes = leaveTypeDao.getLeaveTypesByCarryForwardEnable(true, leaveTypeIds);
		List<LeaveEntitlement> carryForwardedEntitlements = new ArrayList<>();
		List<LeaveEntitlement> oldEntitlements = new ArrayList<>();

		if (!leaveTypes.isEmpty()) {

			leaveTypes.forEach(leaveType -> {
				List<Employee> employees = leaveEntitlementDao
					.findAllEmployeeIdsWithForwardingEntitlements(leaveType.getTypeId(), true, leaveCycleEndDate);

				if (!employees.isEmpty()) {
					employees.forEach(employeeToForward -> {

						if (employeeToForward.getEmployeeId() == null) {
							log.error("Employee ID is null for employee: {}", employeeToForward);
							throw new ModuleException(PeopleMessageConstant.PEOPLE_ERROR_EMPLOYEE_ID_CANNOT_NULL);
						}

						CarryForwardInfo carryForwardInfo;
						float remainsToForward = 0F;
						float daysToForward = 0F;

						List<LeaveEntitlement> leaveEntitlements = leaveEntitlementDao.findLeaveEntitlements(
								leaveType.getTypeId(), true, leaveCycleEndDate, employeeToForward.getEmployeeId());
						float carryForwardDaysMax;
						double totalDaysAllocated = leaveEntitlements.stream()
							.mapToDouble(LeaveEntitlement::getTotalDaysAllocated)
							.sum();
						double totalDaysUsed = leaveEntitlements.stream()
							.mapToDouble(LeaveEntitlement::getTotalDaysUsed)
							.sum();

						if (Boolean.TRUE.equals(leaveType.getIsCarryForwardRemainingBalanceEnabled())) {
							carryForwardDaysMax = (float) (totalDaysAllocated - totalDaysUsed);
						}
						else {
							carryForwardDaysMax = leaveType.getMaxCarryForwardDays();
						}

						if (!leaveEntitlements.isEmpty()) {
							Optional<CarryForwardInfo> carryForwardInfoOpt = carryForwardInfoDao
								.findByEmployeeEmployeeIdAndLeaveTypeTypeIdAndCycleEndDate(
										employeeToForward.getEmployeeId(), leaveType.getTypeId(), leaveCycleEndDate);
							if (carryForwardInfoOpt.isPresent()
									&& Boolean.TRUE.equals(!leaveType.getIsCarryForwardRemainingBalanceEnabled())) {
								carryForwardInfo = carryForwardInfoOpt.get();
								daysToForward = carryForwardDaysMax > 0
										? carryForwardDaysMax - carryForwardInfoOpt.get().getDays()
										: carryForwardInfoOpt.get().getDays();
								carryForwardInfo.setCycleEndDate(leaveCycleEndDate);
							}
							else {
								float carryForwardTotal = getForwardTotals(leaveEntitlements);
								daysToForward = Math.min(carryForwardTotal, carryForwardDaysMax);
								carryForwardInfo = leaveMapper.employeeLeaveTypeToCarryForwardInfo(employeeToForward,
										leaveType, leaveCycleEndDate);
							}
							remainsToForward = daysToForward;
							remainsToForward = getRemainsToForward(oldEntitlements, remainsToForward,
									leaveEntitlements);
							float totalForwardingDays = carryForwardInfo.getDays() != null
									? carryForwardInfo.getDays() + (daysToForward - remainsToForward)
									: daysToForward - remainsToForward;

							carryForwardInfo.setEmployee(employeeToForward);
							carryForwardInfo.setDays(totalForwardingDays);
							carryForwardInfoDao.save(carryForwardInfo);
						}

						float amountToCarryForward = daysToForward - remainsToForward;
						setCarryForwardedEntitlements(leaveCycleStartDate, leaveCycleEndDate,
								carryForwardedEntitlements, leaveType, employeeToForward, amountToCarryForward);
					});
				}

			});
		}

		leaveEntitlementDao.saveAll(oldEntitlements);
		leaveEntitlementDao.saveAll(carryForwardedEntitlements);

		LeaveEntitlementCarryForwardResponseDto leaveEntitlementCarryForwardResponseDto = new LeaveEntitlementCarryForwardResponseDto();
		leaveEntitlementCarryForwardResponseDto.setMessage(
				messageUtil.getMessage(LeaveMessageConstant.LEAVE_ERROR_LEAVE_ENTITLEMENT_CARRY_FORWARD_SUCCESSFUL));

		log.info("forceCarryForwardEntitlements: execution ended");
		return new ResponseEntityDto(false, leaveEntitlementCarryForwardResponseDto);
	}

	@Override
	public ResponseEntityDto getCarryForwardEntitlements(
			CarryForwardLeaveTypesFilterDto carryForwardLeaveTypesFilterDto) {

		int cycleEndYear = carryForwardLeaveTypesFilterDto.getYear() == 0 ? DateTimeUtils.getCurrentYear()
				: carryForwardLeaveTypesFilterDto.getYear();
		LeaveCycleDetailsDto leaveCycleDetail = leaveCycleService.getLeaveCycleConfigs();
		LocalDate leaveCycleStartDate = DateTimeUtils.getUtcLocalDate(cycleEndYear, leaveCycleDetail.getStartMonth(),
				leaveCycleDetail.getStartDate());
		LocalDate leaveCycleEndDate = DateTimeUtils.calculateEndDateAfterYears(leaveCycleStartDate, 1);

		Pageable pageable = PageRequest.of(carryForwardLeaveTypesFilterDto.getPage(),
				carryForwardLeaveTypesFilterDto.getSize(), Sort.by(carryForwardLeaveTypesFilterDto.getSortOrder(),
						carryForwardLeaveTypesFilterDto.getSortKey().toString()));

		PageDto pageDto = leaveEntitlementDao.findLeaveEntitlementsByLeaveTypesAndActiveState(
				carryForwardLeaveTypesFilterDto.getLeaveTypes(), true, leaveCycleEndDate, pageable);
		List<CarryForwardDetailsResponseDto> entitlementDetailsDtos = getEmployeesWithCarryForwardEntitlements(
				(List<LeaveEntitlement>) pageDto.getItems(), leaveCycleEndDate);
		pageDto.setItems(entitlementDetailsDtos);
		return new ResponseEntityDto(false, pageDto);
	}

	@Override
	public ResponseEntityDto getAllCustomLeaveEntitlements(CustomEntitlementsFilterDto customEntitlementsFilterDto) {
		log.info("getAllCustomLeaveEntitlements: execution started");

		Pageable pageable = PageRequest.of(customEntitlementsFilterDto.getPage(), customEntitlementsFilterDto.getSize(),
				Sort.by(customEntitlementsFilterDto.getSortOrder(),
						customEntitlementsFilterDto.getSortKeySearch().toString()));
		Page<LeaveEntitlement> leaveEntitlementPageable = leaveEntitlementDao.findAllCustomEntitlements(
				customEntitlementsFilterDto.getKeyword(), pageable, customEntitlementsFilterDto.getYear(),
				customEntitlementsFilterDto.getLeaveTypeId());
		PageDto pageDto = pageTransformer.transform(leaveEntitlementPageable);

		List<SummarizedCustomLeaveEntitlementDto> list = leaveMapper
			.summarizedCustomLeaveEntitlementDto(leaveEntitlementPageable.hasContent()
					? leaveEntitlementPageable.getContent() : Collections.emptyList());

		pageDto.setItems(list);

		log.info("getAllCustomLeaveEntitlements: completed successfully");
		return new ResponseEntityDto(false, pageDto);
	}

	@Override
	public ResponseEntityDto addBulkNewLeaveEntitlement(BulkLeaveEntitlementDto bulkLeaveEntitlementDto) {
		log.info("addBulkNewLeaveEntitlement: execution started");

		String currentTenant = bulkContextService.getContext();

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		List<ErrorLogDto> bulkRecordErrorLogs = Collections.synchronizedList(new ArrayList<>());
		LeaveBulkSkippedCountDto leaveBulkSkippedCountDto = new LeaveBulkSkippedCountDto(0);
		BulkStatusSummaryDto bulkStatusSummary = new BulkStatusSummaryDto();

		List<CompletableFuture<Void>> tasks = new ArrayList<>();
		List<List<EntitlementDetailsDto>> chunkedBulkData = CommonModuleUtils
			.chunkData(bulkLeaveEntitlementDto.getEntitlementDetailsList());
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

		for (List<EntitlementDetailsDto> entitlementBulkChunkDtoList : chunkedBulkData) {
			for (EntitlementDetailsDto entitlementBulkDto : entitlementBulkChunkDtoList) {
				CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
					bulkContextService.setContext(currentTenant);
					try {
						transactionTemplate.execute(new TransactionCallbackWithoutResult() {
							@Override
							protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
								createNewLeaveEntitlementFromBulk(bulkLeaveEntitlementDto.getYear(), entitlementBulkDto,
										leaveBulkSkippedCountDto, bulkRecordErrorLogs, bulkStatusSummary);
							}
						});
					}
					catch (Exception e) {
						log.info("Exception occurred when saving entitlement: {}", e.getMessage());
						List<String> errorMessages = Collections.singletonList(e.getMessage());
						bulkRecordErrorLogs.add(createErrorLog(entitlementBulkDto, errorMessages));
						bulkStatusSummary.incrementFailedCount();
					}
				}, executorService);
				tasks.add(task);
			}
		}

		CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
		allTasks.thenRun(executorService::shutdown);
		allTasks.join();

		try {
			if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
				log.error("ExecutorService failed to terminate after 5 minutes, shutting down");
				executorService.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted while waiting for termination of ExecutorService", e);
		}

		BulkResponseDto responseDto = new BulkResponseDto();
		responseDto.setBulkRecordErrorLogs(bulkRecordErrorLogs);
		responseDto.setBulkStatusSummary(bulkStatusSummary);

		log.info("addBulkNewLeaveEntitlement: completed successfully");
		return new ResponseEntityDto(false, responseDto);
	}

	@Override
	public ResponseEntityDto getLeaveEntitlementByDate(
			CustomLeaveEntitlementsFilterDto customLeaveEntitlementsFilterDto) {
		log.info("getLeaveEntitlementByYear: execution started");

		Pageable pageable = PageRequest.of(customLeaveEntitlementsFilterDto.getPage(),
				customLeaveEntitlementsFilterDto.getSize(), Sort.by(customLeaveEntitlementsFilterDto.getSortOrder(),
						customLeaveEntitlementsFilterDto.getSortKey().toString()));

		LeaveCycleDetailsDto leaveCycleDetail = leaveCycleService.getLeaveCycleConfigs();
		LocalDate validFrom = DateTimeUtils.getUtcLocalDate(customLeaveEntitlementsFilterDto.getYear(),
				leaveCycleDetail.getStartMonth(), leaveCycleDetail.getStartDate());
		LocalDate validTo = DateTimeUtils.calculateEndDateAfterYears(validFrom, 1);

		PageDto pageDto = new PageDto();
		List<EntitlementBasicDetailsDto> entitlementDetails = new ArrayList<>();

		if (isCustomLeaveEntitlementInCorrectYearRange(customLeaveEntitlementsFilterDto.getYear())) {
			List<Long> employeeIds;
			if (Boolean.TRUE.equals(customLeaveEntitlementsFilterDto.getIsExport())) {
				employeeIds = leaveEntitlementDao.findAllEmployeeIdsCreatedWithValidDates(validFrom, validTo,
						Sort.by(Sort.Direction.ASC, CustomLeaveEntitlementSort.CREATED_DATE.getSortField()));
			}
			else {
				employeeIds = leaveEntitlementDao.findEmployeeIdsCreatedWithValidDates(validFrom, validTo,
						pageable.getPageSize(), pageable.getOffset());
			}

			List<LeaveEntitlement> list = leaveEntitlementDao.findLeaveEntitlementByValidDate(validFrom, validTo,
					pageable.getSort(), employeeIds);

			if (!list.isEmpty()) {
				entitlementDetails = getEmployeesWithEntitlements(list);
			}

			if (Boolean.FALSE.equals(customLeaveEntitlementsFilterDto.getIsExport())) {
				Long totalItems = leaveEntitlementDao.findEmployeeIdsCountCreatedWithValidDates(validFrom, validTo);
				pageDto.setTotalItems(totalItems);
				pageDto.setCurrentPage(customLeaveEntitlementsFilterDto.getPage());
				pageDto.setTotalPages(customLeaveEntitlementsFilterDto.getSize() == 0 ? 1
						: (int) Math.ceil((double) totalItems / (double) customLeaveEntitlementsFilterDto.getSize()));
				pageDto.setItems(entitlementDetails);
			}
		}

		log.info("getLeaveEntitlementByYear: execution ended");
		return Boolean.TRUE.equals(customLeaveEntitlementsFilterDto.getIsExport())
				? new ResponseEntityDto(false, entitlementDetails) : new ResponseEntityDto(false, pageDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntityDto getCurrentUserLeaveEntitlements(LeaveEntitlementsFilterDto leaveEntitlementsFilterDto) {
		log.info("getCurrentUserLeaveEntitlements: execution started");

		User currentUser = userService.getCurrentUser();

		if (leaveEntitlementsFilterDto.getStartDate() != null && leaveEntitlementsFilterDto.getEndDate() != null
				&& leaveEntitlementsFilterDto.getEndDate().isBefore(leaveEntitlementsFilterDto.getStartDate())) {
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_START_DATE_END_DATE_NOT_VALID);
		}
		Long employeeId = currentUser.getUserId();

		List<LeaveEntitlement> leaveEntitlements = leaveEntitlementDao.findAllByEmployeeId(employeeId,
				leaveEntitlementsFilterDto);

		LinkedHashMap<Long, LeaveEntitlementResponseDto> responseDtoList = new LinkedHashMap<>();

		leaveEntitlements.forEach(leaveEntitlement -> processLeaveEntitlements(leaveMapper, peopleMapper,
				responseDtoList, leaveEntitlement));

		log.info("getCurrentUserLeaveEntitlements: execution ended");
		return new ResponseEntityDto(false, responseDtoList.values());
	}

	@Override
	public ResponseEntityDto getCurrentUserLeaveEntitlementBalance(Long id) {

		log.info("getCurrentUserLeaveEntitlementBalance: execution started");

		User currentUser = userService.getCurrentUser();

		Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findById(id);
		if (leaveTypeOpt.isEmpty()) {
			throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_TYPE_NOT_FOUND);
		}

		List<LeaveEntitlement> leaveEntitlements = leaveEntitlementDao
			.getEmployeeLeaveBalanceForLeaveType(currentUser.getUserId(), id);
		log.info("getCurrentUserLeaveEntitlementBalance: execution ended");
		return new ResponseEntityDto(false,
				leaveMapper.leaveEntitlementsToSummarizedLeaveEntitlementBalanceDto(leaveEntitlements));
	}

	public static void processLeaveEntitlements(LeaveMapper mapStructMapper, PeopleMapper peopleMapper,
			Map<Long, LeaveEntitlementResponseDto> responseDtoList, LeaveEntitlement entitlement) {
		long typeID = entitlement.getLeaveType().getTypeId();
		LeaveEntitlementResponseDto filterResponseListDto = responseDtoList.get(typeID);

		if (filterResponseListDto != null) {
			filterResponseListDto.setTotalDaysAllocated(
					filterResponseListDto.getTotalDaysAllocated() + entitlement.getTotalDaysAllocated());
			filterResponseListDto
				.setTotalDaysUsed(filterResponseListDto.getTotalDaysUsed() + entitlement.getTotalDaysUsed());
			filterResponseListDto.setBalanceInDays(
					filterResponseListDto.getTotalDaysAllocated() - filterResponseListDto.getTotalDaysUsed());
			if (filterResponseListDto.getValidFrom().isAfter(entitlement.getValidFrom())) {
				filterResponseListDto.setValidFrom(getEntitlementValidFromDate(entitlement.getValidFrom()));
			}
			if (filterResponseListDto.getValidTo().isBefore(entitlement.getValidTo())) {
				filterResponseListDto.setValidTo(getEntitlementValidToDate(entitlement.getValidTo()));
			}
		}
		else {
			Float totalAllocatedDays = entitlement.getTotalDaysAllocated();
			Float totalUsedDays = entitlement.getTotalDaysUsed();
			Float leaveBalanceInDays = totalAllocatedDays - totalUsedDays;
			LocalDate validFrom = getEntitlementValidFromDate(entitlement.getValidFrom());
			LocalDate validTo = getEntitlementValidToDate(entitlement.getValidTo());
			LeaveTypeBasicDetailsResponseDto leaveTypeBasicDetailsResponseDto = mapStructMapper
				.leaveTypeToLeaveTypeBasicDetailsResponseDto(entitlement.getLeaveType());
			EmployeeBasicDetailsResponseDto employee = peopleMapper
				.employeeToEmployeeBasicDetailsResponseDto(entitlement.getEmployee());

			responseDtoList.put(typeID,
					new LeaveEntitlementResponseDto(entitlement.getEntitlementId(), leaveTypeBasicDetailsResponseDto,
							validFrom, validTo, entitlement.isActive(), totalAllocatedDays, totalUsedDays,
							leaveBalanceInDays, entitlement.getReason(), entitlement.isManual(),
							entitlement.isOverride(), employee));
		}
	}

	public static LocalDate getEntitlementValidFromDate(LocalDate date) {
		LeaveCycleDetailsDto leaveCycleDetailsDto = new LeaveCycleDetailsDto();
		int cycleEndYear = LeaveModuleUtil.getLeaveCycleEndYear(leaveCycleDetailsDto.getStartMonth() - 1,
				leaveCycleDetailsDto.getStartDate());
		int leaveCycleStartYear = leaveCycleDetailsDto.getStartMonth() == 1 && leaveCycleDetailsDto.getStartDate() == 1
				? cycleEndYear : cycleEndYear - 1;
		return date == null ? DateTimeUtils.getUtcLocalDate(leaveCycleStartYear,
				leaveCycleDetailsDto.getStartMonth() - 1, leaveCycleDetailsDto.getStartDate()) : date;
	}

	public static LocalDate getEntitlementValidToDate(LocalDate date) {
		LeaveCycleDetailsDto leaveCycleDetailsDto = new LeaveCycleDetailsDto();
		int cycleEndYear = LeaveModuleUtil.getLeaveCycleEndYear(leaveCycleDetailsDto.getStartMonth() - 1,
				leaveCycleDetailsDto.getStartDate());
		return date == null ? DateTimeUtils.getUtcLocalDate(cycleEndYear, leaveCycleDetailsDto.getEndMonth() - 1,
				leaveCycleDetailsDto.getEndDate()) : date;
	}

	private boolean isCustomLeaveEntitlementInCorrectYearRange(int year) {
		return leaveCycleService.isInCurrentCycle(year) || leaveCycleService.isInPreviousCycle(year)
				|| leaveCycleService.isInNextCycle(year);
	}

	private List<EntitlementBasicDetailsDto> getEmployeesWithEntitlements(List<LeaveEntitlement> allEntitlements) {
		HashMap<Long, EntitlementBasicDetailsDto> results = new HashMap<>();
		List<CustomEntitlementDto> entitlements;

		for (LeaveEntitlement leaveEntitlement : allEntitlements) {
			CustomEntitlementDto newEntitlementDto = leaveMapper
				.leaveTypeAndEntitlementToEntitlementDto(leaveEntitlement.getLeaveType(), leaveEntitlement);

			EntitlementBasicDetailsDto entitlementDetailsDto = new EntitlementBasicDetailsDto();
			entitlements = new ArrayList<>();

			if (results.containsKey(leaveEntitlement.getEmployee().getEmployeeId())) {
				updateEntitlementsIfExistOtherwiseAdd(
						results.get(leaveEntitlement.getEmployee().getEmployeeId()).getEntitlements(),
						newEntitlementDto);
			}
			else {
				entitlements.add(newEntitlementDto);
				entitlementDetailsDto.setEntitlements(entitlements);
				entitlementDetailsDto.setFirstName(leaveEntitlement.getEmployee().getFirstName());
				entitlementDetailsDto.setLastName(leaveEntitlement.getEmployee().getLastName());
				entitlementDetailsDto.setEmail(leaveEntitlement.getEmployee().getUser().getEmail());
				entitlementDetailsDto.setAuthPic(leaveEntitlement.getEmployee().getAuthPic());
				entitlementDetailsDto.setEmployeeId(leaveEntitlement.getEmployee().getUser().getUserId());
				results.put(leaveEntitlement.getEmployee().getEmployeeId(), entitlementDetailsDto);
			}
		}

		return results.values().stream().toList();
	}

	private void updateEntitlementsIfExistOtherwiseAdd(List<CustomEntitlementDto> entitlementDtoList,
			CustomEntitlementDto newEntitlement) {
		if (newEntitlement.getTotalDaysAllocated() == null)
			newEntitlement.setTotalDaysAllocated("0f");
		if (newEntitlement.getLeaveTypeId() != null) {
			for (CustomEntitlementDto existingEntitlement : entitlementDtoList) {
				if (existingEntitlement.getLeaveTypeId().equals(newEntitlement.getLeaveTypeId())) {
					float totalDaysAllocated = Float.parseFloat(newEntitlement.getTotalDaysAllocated())
							+ Float.parseFloat(existingEntitlement.getTotalDaysAllocated());
					existingEntitlement.setTotalDaysAllocated(String.valueOf(totalDaysAllocated));
					return;
				}
			}
		}
		entitlementDtoList.add(newEntitlement);
	}

	private void createNewLeaveEntitlementFromBulk(int year, EntitlementDetailsDto entitlementDetailsDto,
			LeaveBulkSkippedCountDto leaveBulkSkippedCountDto, List<ErrorLogDto> bulkRecordErrorLogs,
			BulkStatusSummaryDto bulkStatusSummary) {

		List<LeaveEntitlement> entitlements = new ArrayList<>();
		LeaveCycleDetailsDto leaveCycleDetail = leaveCycleService.getLeaveCycleConfigs();
		List<String> errors = new ArrayList<>();

		HashMap<String, Boolean> validationMap = validateLeaveEntitlementEntity(entitlementDetailsDto);
		HashMap<String, String> nameValidationMap = validateEntitlementNames(entitlementDetailsDto);
		collectValidationErrors(validationMap, nameValidationMap, errors);

		Optional<User> userByEmailOpt = userDao.findByEmail(entitlementDetailsDto.getEmail());
		if (userByEmailOpt.isEmpty()) {
			errors.add(messageUtil.getMessage(LeaveMessageConstant.USER_IN_BULK_NOT_FOUND,
					new String[] { entitlementDetailsDto.getEmail() }));
		}

		if (!errors.isEmpty()) {
			leaveBulkSkippedCountDto.incrementSkippedCount();
			bulkStatusSummary.incrementFailedCount();
			bulkRecordErrorLogs.add(createErrorLog(entitlementDetailsDto, errors));
			return;
		}

		try {
			Employee employee = userByEmailOpt.get().getEmployee();
			ArrayList<EmployeeTimeline> employeeTimelines = new ArrayList<>();

			for (CustomEntitlementDto customEntitlementDto : entitlementDetailsDto.getEntitlements()) {
				EntitlementDto entitlementDto = leaveMapper.customEntitlementDtoToEntitlementDto(customEntitlementDto);
				LocalDate validFromDate = entitlementDto.getValidFrom() != null ? entitlementDto.getValidFrom()
						: DateTimeUtils.getUtcLocalDate(year, leaveCycleDetail.getStartMonth(),
								leaveCycleDetail.getStartDate());
				LocalDate validToDate = entitlementDto.getValidTo() != null ? entitlementDto.getValidTo()
						: DateTimeUtils.calculateEndDateAfterYears(validFromDate, 1);

				if (entitlementDto.getTotalDaysAllocated() != null) {
					Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findById(entitlementDto.getLeaveTypeId());
					if (leaveTypeOpt.isPresent()) {
						LeaveEntitlement entitlement = leaveMapper
							.entitlementDetailsDtoToLeaveEntitlement(entitlementDetailsDto, entitlementDto);
						entitlement.setEmployee(employee);
						boolean[] updateResult = checkAndUpdateExistingEntitlements(entitlement, validFromDate,
								validToDate);
						boolean isUpdated = updateResult[0];
						boolean logicFailed = updateResult[1];

						if (logicFailed) {
							validationMap = validateLeaveEntitlementEntity(entitlementDetailsDto);
							collectValidationErrors(validationMap, nameValidationMap, errors);
							bulkStatusSummary.incrementFailedCount();
							bulkRecordErrorLogs.add(createErrorLog(entitlementDetailsDto, errors));
							return;
						}

						entitlement.setLeaveType(leaveTypeOpt.get());
						entitlement.setValidFrom(validFromDate);
						entitlement.setValidTo(validToDate);

						if (!isUpdated) {
							entitlements.add(entitlement);
							employeeTimelines.add(getEmployeeTimeline(employee, EmployeeTimelineType.ENTITLEMENT_ADDED,
									PeopleConstants.TITLE_ENTITLEMENT_ADDED, null,
									entitlement.getLeaveType().getName() + " " + entitlement.getTotalDaysAllocated()));
						}

					}
					else {
						errors.add(messageUtil.getMessage(LeaveMessageConstant.LEAVE_ERROR_LEAVE_TYPE_IN_BULK_NOT_FOUND,
								new String[] { entitlementDto.getName() }));
						bulkStatusSummary.incrementFailedCount();
						bulkRecordErrorLogs.add(createErrorLog(entitlementDetailsDto, errors));
						return;
					}
				}
			}

			leaveEntitlementDao.saveAll(entitlements);

			if (!employeeTimelines.isEmpty()) {
				employeeTimelineDao.saveAll(employeeTimelines);
			}

			bulkStatusSummary.incrementSuccessCount();

		}
		catch (Exception e) {
			log.error("Error processing entitlement for employee: {}, error: {}",
					entitlementDetailsDto.getEmployeeName(), e.getMessage());
			bulkStatusSummary.incrementFailedCount();
			bulkRecordErrorLogs.add(createErrorLog(entitlementDetailsDto, List.of(e.getMessage())));
		}
	}

	private void collectValidationErrors(HashMap<String, Boolean> validationMap,
			HashMap<String, String> nameValidationMap, List<String> errors) {
		if (Boolean.FALSE.equals(validationMap.get(LeaveModuleConstant.EMPLOYEE_NAME))) {
			errors.add("Employee Name cannot be null or empty");
		}
		if (Boolean.FALSE.equals(validationMap.get(LeaveModuleConstant.EMPLOYEE_EMAIL))) {
			errors.add("Invalid Employee Email");
		}
		if (Boolean.FALSE.equals(validationMap.get(LeaveModuleConstant.NEGATIVE_ENTITLEMENT))) {
			errors.add("Entitlements cannot have a negative value");
		}
		if (Boolean.FALSE.equals(validationMap.get(LeaveModuleConstant.ENTITLEMENT_LIMIT))) {
			errors.add("The maximum allowed leave entitlement is 360 days");
		}
		if (Boolean.FALSE.equals(validationMap.get(LeaveModuleConstant.VALID_FORMAT))) {
			errors.add("Invalid data format in entitlements");
		}

		if (nameValidationMap.containsKey(LeaveModuleConstant.FULL_DAY_LEAVE)) {
			errors.add(nameValidationMap.get(LeaveModuleConstant.FULL_DAY_LEAVE)
					+ " : Selected leave type only allows full-day leaves");
		}
		if (nameValidationMap.containsKey(LeaveModuleConstant.HALF_DAY_OR_HALF_AND_FULL_DAY_LEAVE)) {
			errors.add(nameValidationMap.get(LeaveModuleConstant.HALF_DAY_OR_HALF_AND_FULL_DAY_LEAVE)
					+ " : Please enter a value in increments of 0.5");
		}
	}

	private ErrorLogDto createErrorLog(EntitlementDetailsDto entitlementDetailsDto, List<String> errors) {
		ErrorLogDto errorLog = new ErrorLogDto();
		errorLog.setEmail(entitlementDetailsDto.getEmail());
		errorLog.setEmployeeId(entitlementDetailsDto.getEmployeeId());
		errorLog.setEmployeeName(entitlementDetailsDto.getEmployeeName());
		errorLog.setEntitlementsDto(entitlementDetailsDto.getEntitlements());
		errorLog.setStatus(BulkItemStatus.ERROR);
		errorLog.setMessage(String.join("; ", errors));
		return errorLog;
	}

	private HashMap<String, Boolean> validateLeaveEntitlementEntity(EntitlementDetailsDto entitlementDetailsDto) {
		HashMap<String, Boolean> validationMap = new HashMap<>();
		validationMap.put(LeaveModuleConstant.EMPLOYEE_NAME, true);
		validationMap.put(LeaveModuleConstant.EMPLOYEE_EMAIL, true);
		validationMap.put(LeaveModuleConstant.ENTITLEMENTS, true);
		validationMap.put(LeaveModuleConstant.NEGATIVE_ENTITLEMENT, true);
		validationMap.put(LeaveModuleConstant.ENTITLEMENT_LIMIT, true);
		validationMap.put(LeaveModuleConstant.VALID_FORMAT, true);

		var totalNumberOfEntitlements = entitlementDetailsDto.getEntitlements().size();
		var count = 0;
		for (CustomEntitlementDto entitlementDto : entitlementDetailsDto.getEntitlements()) {
			if (entitlementDto.getTotalDaysAllocated() == null) {
				count++;
			}
		}
		if (count == totalNumberOfEntitlements) {
			validationMap.replace(LeaveModuleConstant.ENTITLEMENTS, false);
		}

		if (entitlementDetailsDto.getEmployeeName().isEmpty()) {
			validationMap.replace(LeaveModuleConstant.EMPLOYEE_NAME, false);
		}
		if (entitlementDetailsDto.getEmail() == null) {
			validationMap.replace(LeaveModuleConstant.EMPLOYEE_EMAIL, false);
		}
		if (!validateEmployeeEmail(entitlementDetailsDto.getEmail())) {
			validationMap.replace(LeaveModuleConstant.EMPLOYEE_EMAIL, false);
		}
		if (entitlementDetailsDto.getEntitlements()
			.stream()
			.anyMatch(entitlement -> CommonModuleUtils
				.isValidFloat(entitlement.getTotalDaysAllocated()) == Boolean.FALSE)) {
			validationMap.replace(LeaveModuleConstant.VALID_FORMAT, false);
		}
		else {
			if (entitlementDetailsDto.getEntitlements()
				.stream()
				.anyMatch(entitlement -> Float.parseFloat(entitlement.getTotalDaysAllocated()) < 0)) {
				validationMap.replace(LeaveModuleConstant.NEGATIVE_ENTITLEMENT, false);
			}
			if (entitlementDetailsDto.getEntitlements()
				.stream()
				.anyMatch(entitlement -> Float.parseFloat(entitlement.getTotalDaysAllocated()) > 365)) {
				validationMap.replace(LeaveModuleConstant.ENTITLEMENT_LIMIT, false);
			}
		}

		return validationMap;
	}

	private HashMap<String, String> validateEntitlementNames(EntitlementDetailsDto entitlementDetailsDto) {
		HashMap<String, String> nameValidationMap = new HashMap<>();
		for (CustomEntitlementDto entitlementDto : entitlementDetailsDto.getEntitlements()) {
			Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findById(entitlementDto.getLeaveTypeId());
			if (leaveTypeOpt.isPresent() && CommonModuleUtils.isValidFloat(entitlementDto.getTotalDaysAllocated())) {
				LeaveType leaveType = leaveTypeOpt.get();
				if (leaveType.getLeaveDuration() == LeaveDuration.FULL_DAY
						&& Float.parseFloat(entitlementDto.getTotalDaysAllocated()) % 1 != 0) {
					nameValidationMap.put(LeaveModuleConstant.FULL_DAY_LEAVE, entitlementDto.getName());
				}
				else if ((leaveType.getLeaveDuration() == LeaveDuration.HALF_DAY
						|| leaveType.getLeaveDuration() == LeaveDuration.HALF_AND_FULL_DAY)
						&& Float.parseFloat(entitlementDto.getTotalDaysAllocated()) % 0.5 != 0) {
					nameValidationMap.put(LeaveModuleConstant.HALF_DAY_OR_HALF_AND_FULL_DAY_LEAVE,
							entitlementDto.getName());
				}
			}
		}
		return nameValidationMap;
	}

	private boolean[] checkAndUpdateExistingEntitlements(LeaveEntitlement entitlement, LocalDate validFrom,
			LocalDate validTo) {
		float usedDays = 0F;
		float allocatedDays = 0F;
		float currentBalance;
		boolean isUpdated = false;
		boolean logicFailed = false;
		EmployeeTimeline employeeTimeline;

		List<LeaveEntitlement> existingEntitlements = leaveEntitlementDao
			.findByEmployeeAndValidFromAndValidToAndLeaveType(entitlement.getEmployee(), validFrom, validTo,
					entitlement.getLeaveType());
		if (!existingEntitlements.isEmpty()) {
			isUpdated = true;

			for (LeaveEntitlement existingEntitlement : existingEntitlements) {
				currentBalance = existingEntitlement.getTotalDaysAllocated() - existingEntitlement.getTotalDaysUsed();
				if (existingEntitlement.getTotalDaysUsed() <= entitlement.getTotalDaysAllocated()) {
					existingEntitlement.setTotalDaysUsed(existingEntitlement.getTotalDaysUsed());
					existingEntitlement.setTotalDaysAllocated(entitlement.getTotalDaysAllocated());
				}
				else if (existingEntitlement.getTotalDaysUsed() > entitlement.getTotalDaysAllocated()
						|| currentBalance == 0) {
					logicFailed = true;
				}
			}
			leaveEntitlementDao.saveAll(existingEntitlements);
		}

		if (isUpdated) {
			employeeTimeline = getEmployeeTimeline(entitlement.getEmployee(), EmployeeTimelineType.ENTITLEMENT_CHANGED,
					PeopleConstants.TITLE_ENTITLEMENT_UPDATED,
					entitlement.getLeaveType().getName() + " " + allocatedDays,
					entitlement.getLeaveType().getName() + " " + (usedDays + entitlement.getTotalDaysAllocated()));
			employeeTimelineDao.save(employeeTimeline);
		}
		return new boolean[] { isUpdated, logicFailed };
	}

	private List<CarryForwardDetailsResponseDto> getEmployeesWithCarryForwardEntitlements(
			List<LeaveEntitlement> allEntitlements, LocalDate cycleEndDate) {
		HashMap<Long, CarryForwardDetailsResponseDto> results = new HashMap<>();
		List<CarryForwardEntitlementDto> entitlements;

		for (LeaveEntitlement leaveEntitlement : allEntitlements) {
			CarryForwardEntitlementDto newEntitlementDto = leaveMapper
				.leaveTypeAndEntitlementToCarryForwardEntitlementDto(leaveEntitlement.getLeaveType(), leaveEntitlement);

			entitlements = new ArrayList<>();

			if (results.containsKey(leaveEntitlement.getEmployee().getEmployeeId())) {
				updateAddCarryForwardEntitlements(
						results.get(leaveEntitlement.getEmployee().getEmployeeId()).getEntitlements(),
						newEntitlementDto);
			}
			else {
				entitlements.add(newEntitlementDto);
				CarryForwardDetailsResponseDto carryForwardDetailsResponseDto = leaveMapper
					.leaveEntitlementToCarryForwardDetailsDto(leaveEntitlement);
				carryForwardDetailsResponseDto.setEntitlements(entitlements);
				results.put(leaveEntitlement.getEmployee().getEmployeeId(), carryForwardDetailsResponseDto);
			}
		}
		List<CarryForwardDetailsResponseDto> carryForwardDetailsResponseDtos = results.values().stream().toList();
		setCarryForwardingAmount(cycleEndDate, carryForwardDetailsResponseDtos);
		return carryForwardDetailsResponseDtos;
	}

	private void updateAddCarryForwardEntitlements(List<CarryForwardEntitlementDto> entitlementDtoList,
			CarryForwardEntitlementDto newEntitlement) {
		if (newEntitlement.getTotalDaysAllocated() == null)
			newEntitlement.setTotalDaysAllocated(0f);
		if (newEntitlement.getLeaveTypeId() != null) {
			for (CarryForwardEntitlementDto existingEntitlement : entitlementDtoList) {
				if (existingEntitlement.getLeaveTypeId().equals(newEntitlement.getLeaveTypeId())) {
					float totalDaysAllocated = newEntitlement.getTotalDaysAllocated()
							+ existingEntitlement.getTotalDaysAllocated();
					existingEntitlement.setTotalDaysAllocated(totalDaysAllocated);
					return;
				}
			}
		}
		entitlementDtoList.add(newEntitlement);
	}

	private void setCarryForwardingAmount(LocalDate leaveCycleEndDate,
			List<CarryForwardDetailsResponseDto> carryForwardDetailsResponseDtos) {
		carryForwardDetailsResponseDtos.forEach(detail -> detail.getEntitlements().forEach(entitlementDto -> {
			Optional<LeaveType> leaveType = leaveTypeDao.findById(entitlementDto.getLeaveTypeId());
			if (leaveType.isEmpty()) {
				throw new EntityNotFoundException(LeaveMessageConstant.LEAVE_ERROR_LEAVE_TYPE_NOT_FOUND);
			}

			Optional<CarryForwardInfo> carryForwardInfo = carryForwardInfoDao
				.findByEmployeeEmployeeIdAndLeaveTypeTypeIdAndCycleEndDate(detail.getEmployee().getEmployeeId(),
						entitlementDto.getLeaveTypeId(), leaveCycleEndDate);
			if (Boolean.FALSE.equals(leaveType.get().getIsCarryForwardRemainingBalanceEnabled())) {
				if (carryForwardInfo.isPresent()) {
					entitlementDto.setCarryForwardAmount(
							Math.min(leaveType.get().getMaxCarryForwardDays() - carryForwardInfo.get().getDays(),
									(entitlementDto.getTotalDaysAllocated() - entitlementDto.getTotalDaysUsed())));
				}
				else {
					entitlementDto.setCarryForwardAmount(Math.min(leaveType.get().getMaxCarryForwardDays(),
							(entitlementDto.getTotalDaysAllocated() - entitlementDto.getTotalDaysUsed())));
				}
			}
			else {
				entitlementDto
					.setCarryForwardAmount(entitlementDto.getTotalDaysAllocated() - entitlementDto.getTotalDaysUsed());
			}
		}));
	}

	private void setCarryForwardedEntitlements(LocalDate leaveCycleStartDate, LocalDate leaveCycleEndDate,
			List<LeaveEntitlement> carryForwardedEntitlements, LeaveType leaveType, Employee employeeToForward,
			float amountToCarryForward) {
		if (amountToCarryForward > 0) {
			LeaveEntitlement newLeaveEntitlement = leaveMapper.employeeToLeaveEntitlement(employeeToForward);
			setLeaveEntitlementLeaveType(leaveType.getTypeId(), newLeaveEntitlement);
			newLeaveEntitlement.setTotalDaysAllocated(amountToCarryForward);

			newLeaveEntitlement.setValidFrom(DateTimeUtils.incrementYearByOne(leaveCycleStartDate));
			if (leaveType.getCarryForwardExpirationDays() > 0) {
				newLeaveEntitlement.setValidTo(DateTimeUtils.incrementDays(leaveCycleEndDate,
						(int) leaveType.getCarryForwardExpirationDays()));
			}
			else {
				newLeaveEntitlement.setValidTo(DateTimeUtils.incrementYearByOne(leaveCycleEndDate));
			}
			carryForwardedEntitlements.add(newLeaveEntitlement);
		}
	}

	private float getRemainsToForward(List<LeaveEntitlement> oldEntitlements, float remainsToForward,
			List<LeaveEntitlement> leaveEntitlements) {
		for (LeaveEntitlement leaveEntitlement : leaveEntitlements) {
			if (remainsToForward != 0) {
				leaveEntitlement.setReason(LeaveModuleConstant.DISCARD_LEAVE_REQUEST_REASON);
				if ((leaveEntitlement.getTotalDaysAllocated()
						- leaveEntitlement.getTotalDaysUsed()) >= remainsToForward) {
					leaveEntitlement.setTotalDaysAllocated(leaveEntitlement.getTotalDaysAllocated() - remainsToForward);
					remainsToForward = 0F;
				}
				else {
					float totalDaysAllocated = leaveEntitlement.getTotalDaysAllocated();
					float totalDaysUsed = leaveEntitlement.getTotalDaysUsed();
					leaveEntitlement.setTotalDaysAllocated(totalDaysUsed);
					remainsToForward = remainsToForward - (totalDaysAllocated - totalDaysUsed);
				}
				oldEntitlements.add(leaveEntitlement);
			}
		}
		return remainsToForward;
	}

	private float getForwardTotals(List<LeaveEntitlement> leaveEntitlements) {
		return leaveEntitlements.stream()
			.map(leaveEntitlement -> leaveEntitlement.getTotalDaysAllocated() - leaveEntitlement.getTotalDaysUsed())
			.reduce(0F, Float::sum);
	}

	private EmployeeTimeline getEmployeeTimeline(Employee employee, EmployeeTimelineType timelineType, String title,
			String previousValue, String newValue) {
		EmployeeTimeline timeline = new EmployeeTimeline();
		timeline.setEmployee(employee);
		timeline.setTimelineType(timelineType);
		timeline.setTitle(title);
		timeline.setPreviousValue(previousValue);
		timeline.setNewValue(newValue);
		timeline.setDisplayDate(DateTimeUtils.getCurrentUtcDate());
		return timeline;
	}

	private boolean validateEmployeeEmail(String email) {
		Optional<User> userOpt = userDao.findByEmail(email);
		return userOpt.isPresent();
	}

}
