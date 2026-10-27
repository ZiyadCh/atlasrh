package ma.atlasrh.atlasrh.leave.service;

import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
import ma.atlasrh.atlasrh.leave.mapper.LeaveRequestMapper;
import ma.atlasrh.atlasrh.leave.repository.LeaveRequestRepository;
import ma.atlasrh.atlasrh.leave.repository.LeaveTypeRepository;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    LeaveRequest entity = leaveRequestMapper.toEntity(dto);
    entity.setEmployee(employeeRepository.findById(employeeId)
        .orElseThrow(() -> new IllegalStateException("Employee not found: " + employeeId)));
    entity.setLeaveType(leaveTypeRepository.findById(dto.leaveTypeId())
        .orElseThrow(() -> new IllegalArgumentException("Leave type not found: " + dto.leaveTypeId())));
    entity.setStatus(LeaveRequestStatus.EN_ATTENTE);
    return leaveRequestRepository.save(entity);
  }
}
