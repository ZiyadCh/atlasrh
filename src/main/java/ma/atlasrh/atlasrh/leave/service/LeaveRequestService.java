package ma.atlasrh.atlasrh.leave.service;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;
import ma.atlasrh.atlasrh.leave.dto.ChangeLeaveStatus;
import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.dto.GetLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
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
  public LeaveRequest create(CreateLeaveRequest dto, Long employeeId) {
    authorizationService.employeeLeaveRights(employeeId);
    LeaveRequest lRequest = leaveRequestMapper.toEntity(dto);
    int days = (int) lRequest.getStartDate().datesUntil(lRequest.getEndDate().plusDays(1))
        .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY)
        .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
        .count();
    lRequest.setJourTravail(days);
    ////
    lRequest.setEmployee(employeeRepository.findById(employeeId)
        .orElseThrow(() -> new IllegalStateException("Employee not found: " + employeeId)));
    lRequest.setLeaveType(leaveTypeRepository.findById(dto.leaveTypeId())
        .orElseThrow(() -> new IllegalArgumentException("Leave type not found: " + dto.leaveTypeId())));
    lRequest.setStatus(LeaveRequestStatus.EN_ATTENTE);
    ///
    return leaveRequestRepository.save(lRequest);
  }

  @Transactional
  public LeaveRequest changeStatus(ChangeLeaveStatus dto, Long employeeId) {
    authorizationService.leaveRequestRights(employeeId);
    LeaveRequest lRequest = leaveRequestRepository.findById(dto.leaveId())
        .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + dto.leaveId()));
    if (lRequest.getStatus() != LeaveRequestStatus.EN_ATTENTE) {
      throw new IllegalArgumentException("Only a pending leave request can be approved or refused");
    }
    if (lRequest.getEmployee().getManager() == null
        || !lRequest.getEmployee().getManager().getId().equals(employeeId)) {
      throw new NoAccessException("You can only decide on leave requests of your own team");
    }
    if (dto.status() == LeaveRequestStatus.REFUSEE
        && (dto.managerComment() == null || dto.managerComment().isBlank())) {
      throw new IllegalArgumentException("A refusal requires a manager comment");
    }
    lRequest.setStatus(dto.status());
    lRequest.setManagerComment(dto.managerComment());
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

  @Transactional
  public void cancel(Long leaveId, Long employeeId) {
    authorizationService.employeeLeaveRights(employeeId);
    LeaveRequest lRequest = leaveRequestRepository.findById(leaveId)
        .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + leaveId));
    if (!lRequest.getEmployee().getId().equals(employeeId)) {
      throw new NoAccessException("You can only cancel your own leave requests");
    }
    if (lRequest.getStatus() != LeaveRequestStatus.EN_ATTENTE) {
      throw new IllegalArgumentException("Only a pending leave request can be cancelled");
    }
    lRequest.setStatus(LeaveRequestStatus.ANNULEE);
    leaveRequestRepository.save(lRequest);
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
