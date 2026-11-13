package ma.atlasrh.atlasrh.leave.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ma.atlasrh.atlasrh.employee.entity.Employee;
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

/**
 * LeaveRequestServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {
  @Mock
  private LeaveRequestRepository leaveRequestRepository;
  @Mock
  private LeaveTypeRepository leaveTypeRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private LeaveRequestMapper leaveRequestMapper;
  @Mock
  private AuthorizationService authorizationService;
  @InjectMocks
  private LeaveRequestService leaveRequestService;

  @Test
  void create_shouldSaveAndReturnLeaveRequest() {
    Long employeeId = 1L;
    Long leaveTypeId = 2L;
    CreateLeaveRequest dto = new CreateLeaveRequest(LocalDate.of(2025, 1, 6),
        LocalDate.of(2025, 1, 8), "Vacances", leaveTypeId);

    LeaveRequest mapped = new LeaveRequest();
    mapped.setStartDate(LocalDate.of(2025, 1, 6));
    mapped.setEndDate(LocalDate.of(2025, 1, 8));
    mapped.setReason("Vacances");

    LeaveType leaveType = new LeaveType();
    leaveType.setId(leaveTypeId);
    leaveType.setDaysPerYear(20);

    Employee employee = new Employee();
    employee.setId(employeeId);

    when(leaveRequestMapper.toEntity(dto)).thenReturn(mapped);
    when(leaveTypeRepository.findById(leaveTypeId)).thenReturn(Optional.of(leaveType));
    when(leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        eq(employeeId), eq(LeaveRequestStatus.APPROUVEE), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
    when(leaveRequestRepository.save(mapped)).thenReturn(mapped);

    LeaveRequest result = leaveRequestService.create(dto, employeeId);

    assertThat(result).isEqualTo(mapped);
    assertThat(result.getJourTravail()).isEqualTo(3);
    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.EN_ATTENTE);
    assertThat(result.getLeaveType()).isEqualTo(leaveType);
    assertThat(result.getEmployee()).isEqualTo(employee);
    verify(authorizationService).employeeLeaveRights(employeeId);
    verify(leaveRequestRepository).save(mapped);
  }

  @Test
  void create_shouldThrowWhenStartDateIsAfterEndDate() {
    Long employeeId = 1L;
    CreateLeaveRequest dto = new CreateLeaveRequest(LocalDate.of(2025, 1, 8),
        LocalDate.of(2025, 1, 6), "Vacances", 2L);

    LeaveRequest mapped = new LeaveRequest();
    mapped.setStartDate(LocalDate.of(2025, 1, 8));
    mapped.setEndDate(LocalDate.of(2025, 1, 6));

    when(leaveRequestMapper.toEntity(dto)).thenReturn(mapped);

    assertThatThrownBy(() -> leaveRequestService.create(dto, employeeId))
        .isInstanceOf(InvalidDateException.class);

    verify(leaveRequestRepository, never()).save(any());
  }

  @Test
  void create_shouldThrowWhenLeaveTypeNotFound() {
    Long employeeId = 1L;
    Long leaveTypeId = 2L;
    CreateLeaveRequest dto = new CreateLeaveRequest(LocalDate.of(2025, 1, 6),
        LocalDate.of(2025, 1, 8), "Vacances", leaveTypeId);

    LeaveRequest mapped = new LeaveRequest();
    mapped.setStartDate(LocalDate.of(2025, 1, 6));
    mapped.setEndDate(LocalDate.of(2025, 1, 8));

    when(leaveRequestMapper.toEntity(dto)).thenReturn(mapped);
    when(leaveTypeRepository.findById(leaveTypeId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> leaveRequestService.create(dto, employeeId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_shouldThrowWhenNotEnoughDaysLeft() {
    Long employeeId = 1L;
    Long leaveTypeId = 2L;
    CreateLeaveRequest dto = new CreateLeaveRequest(LocalDate.of(2025, 1, 6),
        LocalDate.of(2025, 1, 8), "Vacances", leaveTypeId);

    LeaveRequest mapped = new LeaveRequest();
    mapped.setStartDate(LocalDate.of(2025, 1, 6));
    mapped.setEndDate(LocalDate.of(2025, 1, 8));

    LeaveType leaveType = new LeaveType();
    leaveType.setId(leaveTypeId);
    leaveType.setDaysPerYear(20);

    LeaveRequest approved = new LeaveRequest();
    approved.setJourTravail(20);
    approved.setLeaveType(leaveType);

    when(leaveRequestMapper.toEntity(dto)).thenReturn(mapped);
    when(leaveTypeRepository.findById(leaveTypeId)).thenReturn(Optional.of(leaveType));
    when(leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        eq(employeeId), eq(LeaveRequestStatus.APPROUVEE), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(approved));

    assertThatThrownBy(() -> leaveRequestService.create(dto, employeeId))
        .isInstanceOf(IllegalArgumentException.class);

    verify(leaveRequestRepository, never()).save(any());
  }

  @Test
  void create_shouldThrowWhenNoAccess() {
    Long employeeId = 1L;
    CreateLeaveRequest dto = new CreateLeaveRequest(LocalDate.of(2025, 1, 6),
        LocalDate.of(2025, 1, 8), "Vacances", 2L);

    doThrow(new NoAccessException("No access to employee leaves"))
        .when(authorizationService).employeeLeaveRights(employeeId);

    assertThatThrownBy(() -> leaveRequestService.create(dto, employeeId))
        .isInstanceOf(NoAccessException.class);

    verify(leaveRequestMapper, never()).toEntity(any());
    verify(leaveRequestRepository, never()).save(any());
  }

  @Test
  void remainingBalance_shouldReturnRemainingDays() {
    Long employeeId = 1L;
    Long leaveTypeId = 2L;
    int year = 2025;

    LeaveType leaveType = new LeaveType();
    leaveType.setId(leaveTypeId);
    leaveType.setDaysPerYear(20);

    LeaveRequest approved = new LeaveRequest();
    approved.setJourTravail(5);
    approved.setLeaveType(leaveType);

    when(leaveTypeRepository.findById(leaveTypeId)).thenReturn(Optional.of(leaveType));
    when(leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        employeeId, LeaveRequestStatus.APPROUVEE, LocalDate.of(year, 1, 1),
        LocalDate.of(year, 12, 31))).thenReturn(List.of(approved));

    int result = leaveRequestService.remainingBalance(employeeId, leaveTypeId, year);

    assertThat(result).isEqualTo(15);
  }

  @Test
  void remainingBalance_shouldThrowWhenLeaveTypeNotFound() {
    when(leaveTypeRepository.findById(2L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> leaveRequestService.remainingBalance(1L, 2L, 2025))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getBalances_shouldReturnBalances() {
    Long employeeId = 1L;

    LeaveType annual = new LeaveType();
    annual.setId(1L);
    annual.setName("Congé annuel");
    annual.setDaysPerYear(20);

    LeaveType sick = new LeaveType();
    sick.setId(2L);
    sick.setName("Maladie");
    sick.setDaysPerYear(10);

    LeaveRequest approved = new LeaveRequest();
    approved.setJourTravail(5);
    approved.setLeaveType(annual);

    when(leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        eq(employeeId), eq(LeaveRequestStatus.APPROUVEE), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(approved));
    when(leaveTypeRepository.findAll()).thenReturn(List.of(annual, sick));

    List<LeaveBalanceDTO> result = leaveRequestService.getBalances(employeeId);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).leaveTypeName()).isEqualTo("Congé annuel");
    assertThat(result.get(0).daysUsed()).isEqualTo(5);
    assertThat(result.get(0).daysRemaining()).isEqualTo(15);
    assertThat(result.get(1).leaveTypeName()).isEqualTo("Maladie");
    assertThat(result.get(1).daysRemaining()).isEqualTo(10);
    verify(authorizationService).employeeLeaveRights(employeeId);
  }

  @Test
  void changeStatus_shouldUpdateStatus() {
    Long managerId = 1L;
    Long leaveId = 5L;
    ChangeLeaveStatus dto = new ChangeLeaveStatus(leaveId, LeaveRequestStatus.APPROUVEE, "Ok");

    LeaveRequest request = leaveRequest(leaveId, LeaveRequestStatus.EN_ATTENTE, 2L);

    when(leaveRequestRepository.findById(leaveId)).thenReturn(Optional.of(request));
    when(leaveRequestRepository.save(request)).thenReturn(request);

    LeaveRequest result = leaveRequestService.changeStatus(dto, managerId);

    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.APPROUVEE);
    assertThat(result.getManagerComment()).isEqualTo("Ok");
    verify(authorizationService).leaveRequestRights(managerId);
    verify(leaveRequestRepository).save(request);
  }

  @Test
  void changeStatus_shouldThrowWhenNotFound() {
    ChangeLeaveStatus dto = new ChangeLeaveStatus(5L, LeaveRequestStatus.APPROUVEE, "Ok");

    when(leaveRequestRepository.findById(5L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> leaveRequestService.changeStatus(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void changeStatus_shouldThrowWhenNotPending() {
    Long leaveId = 5L;
    ChangeLeaveStatus dto = new ChangeLeaveStatus(leaveId, LeaveRequestStatus.APPROUVEE, "Ok");

    LeaveRequest request = leaveRequest(leaveId, LeaveRequestStatus.APPROUVEE, 2L);

    when(leaveRequestRepository.findById(leaveId)).thenReturn(Optional.of(request));

    assertThatThrownBy(() -> leaveRequestService.changeStatus(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);

    verify(leaveRequestRepository, never()).save(any());
  }

  @Test
  void cancelRequest_shouldCancelLeaveRequest() {
    Long employeeId = 1L;
    Long leaveId = 5L;
    CancelLeaveRequestDTO dto = new CancelLeaveRequestDTO(leaveId, LeaveRequestStatus.ANNULEE);

    LeaveRequest request = leaveRequest(leaveId, LeaveRequestStatus.EN_ATTENTE, employeeId);

    when(leaveRequestRepository.findById(leaveId)).thenReturn(Optional.of(request));
    when(leaveRequestRepository.save(request)).thenReturn(request);

    LeaveRequest result = leaveRequestService.cancelRequest(dto, employeeId);

    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.ANNULEE);
    verify(authorizationService).employeeLeaveRights(employeeId);
    verify(leaveRequestRepository).save(request);
  }

  @Test
  void cancelRequest_shouldThrowWhenNotOwner() {
    Long employeeId = 1L;
    Long leaveId = 5L;
    CancelLeaveRequestDTO dto = new CancelLeaveRequestDTO(leaveId, LeaveRequestStatus.ANNULEE);

    LeaveRequest request = leaveRequest(leaveId, LeaveRequestStatus.EN_ATTENTE, 99L);

    when(leaveRequestRepository.findById(leaveId)).thenReturn(Optional.of(request));

    assertThatThrownBy(() -> leaveRequestService.cancelRequest(dto, employeeId))
        .isInstanceOf(IllegalArgumentException.class);

    verify(leaveRequestRepository, never()).save(any());
  }

  @Test
  void cancelRequest_shouldThrowWhenNotPending() {
    Long employeeId = 1L;
    Long leaveId = 5L;
    CancelLeaveRequestDTO dto = new CancelLeaveRequestDTO(leaveId, LeaveRequestStatus.ANNULEE);

    LeaveRequest request = leaveRequest(leaveId, LeaveRequestStatus.APPROUVEE, employeeId);

    when(leaveRequestRepository.findById(leaveId)).thenReturn(Optional.of(request));

    assertThatThrownBy(() -> leaveRequestService.cancelRequest(dto, employeeId))
        .isInstanceOf(IllegalArgumentException.class);

    verify(leaveRequestRepository, never()).save(any());
  }

  @Test
  void findTeamPending_shouldReturnRequests() {
    Long managerId = 1L;
    LeaveRequest request = leaveRequest(5L, LeaveRequestStatus.EN_ATTENTE, 2L);
    GetLeaveRequestDTO dto = getDto(5L, LeaveRequestStatus.EN_ATTENTE, 2L);

    when(leaveRequestRepository.findByEmployee_Manager_IdAndStatus(managerId,
        LeaveRequestStatus.EN_ATTENTE)).thenReturn(List.of(request));
    when(leaveRequestMapper.toDto(request)).thenReturn(dto);

    List<GetLeaveRequestDTO> result = leaveRequestService.findTeamPending(managerId);

    assertThat(result).containsExactly(dto);
    verify(authorizationService).leaveRequestRights(managerId);
  }

  @Test
  void findMyRequests_shouldFilterByStatus() {
    Long employeeId = 1L;
    LeaveRequest request = leaveRequest(5L, LeaveRequestStatus.APPROUVEE, employeeId);
    GetLeaveRequestDTO dto = getDto(5L, LeaveRequestStatus.APPROUVEE, employeeId);

    when(leaveRequestRepository.findByEmployee_IdAndStatus(employeeId,
        LeaveRequestStatus.APPROUVEE)).thenReturn(List.of(request));
    when(leaveRequestMapper.toDto(request)).thenReturn(dto);

    List<GetLeaveRequestDTO> result = leaveRequestService.findMyRequests(employeeId,
        LeaveRequestStatus.APPROUVEE);

    assertThat(result).containsExactly(dto);
    verify(authorizationService).employeeLeaveRights(employeeId);
  }

  @Test
  void findMyRequests_shouldReturnAllWhenStatusIsNull() {
    Long employeeId = 1L;
    LeaveRequest request = leaveRequest(5L, LeaveRequestStatus.EN_ATTENTE, employeeId);
    GetLeaveRequestDTO dto = getDto(5L, LeaveRequestStatus.EN_ATTENTE, employeeId);

    when(leaveRequestRepository.findByEmployee_Id(employeeId)).thenReturn(List.of(request));
    when(leaveRequestMapper.toDto(request)).thenReturn(dto);

    List<GetLeaveRequestDTO> result = leaveRequestService.findMyRequests(employeeId, null);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void findAll_shouldReturnRequests() {
    Long managerId = 1L;
    LeaveRequest request = leaveRequest(5L, LeaveRequestStatus.EN_ATTENTE, 2L);
    GetLeaveRequestDTO dto = getDto(5L, LeaveRequestStatus.EN_ATTENTE, 2L);

    when(leaveRequestRepository.findAll()).thenReturn(List.of(request));
    when(leaveRequestMapper.toDto(request)).thenReturn(dto);

    List<GetLeaveRequestDTO> result = leaveRequestService.findAll(managerId);

    assertThat(result).containsExactly(dto);
    verify(authorizationService).leaveRequestRights(managerId);
  }

  @Test
  void findById_shouldReturnRequest() {
    Long leaveId = 5L;
    LeaveRequest request = leaveRequest(leaveId, LeaveRequestStatus.EN_ATTENTE, 2L);
    GetLeaveRequestDTO dto = getDto(leaveId, LeaveRequestStatus.EN_ATTENTE, 2L);

    when(leaveRequestRepository.findById(leaveId)).thenReturn(Optional.of(request));
    when(leaveRequestMapper.toDto(request)).thenReturn(dto);

    GetLeaveRequestDTO result = leaveRequestService.findById(leaveId, 1L);

    assertThat(result).isEqualTo(dto);
    verify(authorizationService).leaveRequestRights(1L);
  }

  @Test
  void findById_shouldThrowWhenNotFound() {
    when(leaveRequestRepository.findById(5L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> leaveRequestService.findById(5L, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private LeaveRequest leaveRequest(Long id, LeaveRequestStatus status, Long employeeId) {
    Employee employee = new Employee();
    employee.setId(employeeId);

    LeaveRequest request = new LeaveRequest();
    request.setId(id);
    request.setStatus(status);
    request.setEmployee(employee);
    return request;
  }

  private GetLeaveRequestDTO getDto(Long id, LeaveRequestStatus status, Long employeeId) {
    return new GetLeaveRequestDTO(id, LocalDate.of(2025, 1, 6), LocalDate.of(2025, 1, 8), 3,
        "Vacances", status, employeeId);
  }
}
