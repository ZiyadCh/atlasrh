package ma.atlasrh.atlasrh.leave.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;
import ma.atlasrh.atlasrh.leave.dto.CancelLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.dto.ChangeLeaveStatus;
import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.dto.GetLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.dto.LeaveBalanceDTO;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
import ma.atlasrh.atlasrh.leave.entity.LeaveType;
import ma.atlasrh.atlasrh.leave.exceptions.InvalidDateException;
import ma.atlasrh.atlasrh.leave.mapper.LeaveRequestMapper;
import ma.atlasrh.atlasrh.leave.repository.LeaveRequestRepository;
import ma.atlasrh.atlasrh.leave.repository.LeaveTypeRepository;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

  private final LeaveRequestRepository leaveRequestRepository;
  private final LeaveTypeRepository leaveTypeRepository;
  private final EmployeeRepository employeeRepository;
  private final LeaveRequestMapper leaveRequestMapper;
  private final AuthorizationService authorizationService;

  @Transactional
  public LeaveRequest create(CreateLeaveRequest dto, Long employeeId) throws InvalidDateException {
    authorizationService.employeeLeaveRights(employeeId);
    LeaveRequest lRequest = leaveRequestMapper.toEntity(dto);
    if (lRequest.getStartDate().isAfter(lRequest.getEndDate())) {
      throw new InvalidDateException("la date de debut ne peut pas etre apres la date fin");
    }
    int days = (int) lRequest.getStartDate().datesUntil(lRequest.getEndDate().plusDays(1))
        .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY)
        .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
        .count();
    lRequest.setJourTravail(days);
    int remaining = remainingBalance(employeeId, dto.leaveTypeId(), Year.now().getValue());
    if (days > remaining) {
      throw new IllegalArgumentException("Solde insuffisant pour ce type de conge (reste : " + remaining + " jour(s))");
    }

    lRequest.setEmployee(employeeRepository.findById(employeeId)
        .orElseThrow(() -> new IllegalStateException("Employee not found: " + employeeId)));
    LeaveType leaveType = leaveTypeRepository.findById(dto.leaveTypeId())
        .orElseThrow(() -> new IllegalArgumentException("Leave type not found: " + dto.leaveTypeId()));
    lRequest.setLeaveType(leaveType);
    lRequest.setStatus(LeaveRequestStatus.EN_ATTENTE);
    return leaveRequestRepository.save(lRequest);
  }

  public int remainingBalance(Long employeeId, Long leaveTypeId, int year) {
    LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
        .orElseThrow(() -> new IllegalArgumentException("Leave type not found: " + leaveTypeId));
    LocalDate start = LocalDate.of(year, 1, 1);
    LocalDate end = LocalDate.of(year, 12, 31);
    List<LeaveRequest> approved = leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        employeeId, LeaveRequestStatus.APPROUVEE, start, end);
    int daysUsed = approved.stream()
        .filter(r -> r.getLeaveType() != null && r.getLeaveType().getId().equals(leaveTypeId))
        .mapToInt(LeaveRequest::getJourTravail)
        .sum();
    return leaveType.getDaysPerYear() - daysUsed;
  }

  @Transactional(readOnly = true)
  public List<LeaveBalanceDTO> getBalances(Long employeeId) {
    authorizationService.employeeLeaveRights(employeeId);
    int year = Year.now().getValue();
    LocalDate start = LocalDate.of(year, 1, 1);
    LocalDate end = LocalDate.of(year, 12, 31);
    List<LeaveRequest> approved = leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        employeeId, LeaveRequestStatus.APPROUVEE, start, end);

    return leaveTypeRepository.findAll().stream()
        .map(leaveType -> {
          int daysUsed = approved.stream()
              .filter(r -> r.getLeaveType() != null && r.getLeaveType().getId().equals(leaveType.getId()))
              .mapToInt(LeaveRequest::getJourTravail)
              .sum();
          return new LeaveBalanceDTO(
              leaveType.getId(),
              leaveType.getName(),
              leaveType.getDaysPerYear(),
              daysUsed,
              leaveType.getDaysPerYear() - daysUsed);
        })
        .toList();
  }

  @Transactional
  public LeaveRequest changeStatus(ChangeLeaveStatus dto, Long employeeId) {
    authorizationService.leaveRequestRights(employeeId);
    LeaveRequest lRequest = leaveRequestRepository.findById(dto.leaveId())
        .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + dto.leaveId()));

    if (lRequest.getStatus() != LeaveRequestStatus.EN_ATTENTE) {
      throw new IllegalArgumentException("Only a pending leave request can be approved or refused");
    }

    if (lRequest.getStatus() == LeaveRequestStatus.ANNULEE) {
      throw new IllegalArgumentException("Only the employee himself can cancel his request");
    }

    lRequest.setStatus(dto.status());
    lRequest.setManagerComment(dto.managerComment());
    return leaveRequestRepository.save(lRequest);
  }

  @Transactional
  public LeaveRequest cancelRequest(CancelLeaveRequestDTO dto, Long employeeId) {
    authorizationService.employeeLeaveRights(employeeId);
    LeaveRequest lRequest = leaveRequestRepository.findById(dto.leaveId())
        .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + dto.leaveId()));
    if (lRequest.getEmployee().getId() != employeeId) {
      throw new IllegalArgumentException("this is not one of your requests");
    }

    if (lRequest.getStatus() != LeaveRequestStatus.EN_ATTENTE) {
      throw new IllegalArgumentException("Only a pending leave request can be approved or refused");
    }

    if (lRequest.getStatus() != LeaveRequestStatus.ANNULEE) {
      throw new IllegalArgumentException("you can only cancel the request");
    }

    lRequest.setStatus(dto.status());
    return leaveRequestRepository.save(lRequest);
  }

  @Transactional(readOnly = true)
  public List<GetLeaveRequestDTO> findTeamPending(Long managerId) {
    authorizationService.leaveRequestRights(managerId);
    List<LeaveRequest> requests = leaveRequestRepository.findByEmployee_Manager_IdAndStatus(
        managerId, LeaveRequestStatus.EN_ATTENTE);
    return requests.stream().map(leaveRequestMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<GetLeaveRequestDTO> findMyRequests(Long employeeId, LeaveRequestStatus status) {
    authorizationService.employeeLeaveRights(employeeId);
    List<LeaveRequest> requests;
    if (status != null) {
      requests = leaveRequestRepository.findByEmployee_IdAndStatus(employeeId, status);
    } else {
      requests = leaveRequestRepository.findByEmployee_Id(employeeId);
    }
    return requests.stream().map(leaveRequestMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<GetLeaveRequestDTO> findAll(Long employeeId) {
    authorizationService.leaveRequestRights(employeeId);
    return leaveRequestRepository.findAll().stream()
        .map(leaveRequestMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public GetLeaveRequestDTO findById(Long id, Long employeeId) {
    authorizationService.leaveRequestRights(employeeId);
    return leaveRequestRepository.findById(id)
        .map(leaveRequestMapper::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + id));
  }
}
