package ma.atlasrh.atlasrh.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ma.atlasrh.atlasrh.contract.dto.CreateContractDTO;
import ma.atlasrh.atlasrh.contract.dto.GetContractDTO;
import ma.atlasrh.atlasrh.contract.entity.Contract;
import ma.atlasrh.atlasrh.contract.entity.ContractStatus;
import ma.atlasrh.atlasrh.contract.entity.ContractType;
import ma.atlasrh.atlasrh.contract.mapper.ContractMapper;
import ma.atlasrh.atlasrh.contract.repository.ContractRepository;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;

/**
 * ContractServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

  @Mock
  ContractRepository contractRepository;
  @Mock
  ContractMapper contractMapper;
  @Mock
  AuthorizationService authorizationService;
  @Mock
  EmployeeRepository employeeRepository;
  @InjectMocks
  ContractService contractService;

  @Test
  void create_shouldSaveAndReturnContract() {
    Long employeeId = 1L;
    Long targetEmployeeId = 2L;
    CreateContractDTO dto = new CreateContractDTO(ContractType.CDI, LocalDate.of(2025, 1, 1), null,
        new BigDecimal("5000.00"), targetEmployeeId);

    Employee employee = new Employee();
    employee.setId(targetEmployeeId);

    Contract contract = new Contract();
    contract.setContractType(ContractType.CDI);
    contract.setStartDate(LocalDate.of(2025, 1, 1));
    contract.setSalaireBrut(new BigDecimal("5000.00"));

    Contract saved = new Contract();
    saved.setId(5L);
    saved.setContractType(ContractType.CDI);
    saved.setStartDate(LocalDate.of(2025, 1, 1));
    saved.setSalaireBrut(new BigDecimal("5000.00"));
    saved.setEmployee(employee);

    when(contractMapper.toEntity(dto)).thenReturn(contract);
    when(employeeRepository.findById(targetEmployeeId)).thenReturn(Optional.of(employee));
    when(contractRepository.save(contract)).thenReturn(saved);

    Contract result = contractService.create(dto, employeeId);

    assertThat(result).isEqualTo(saved);
    assertThat(result.getId()).isEqualTo(5L);
    assertThat(contract.getEmployee()).isEqualTo(employee);
    verify(authorizationService).contractRights(employeeId);
    verify(contractRepository).save(contract);
  }

  @Test
  void create_shouldThrowWhenNoAccess() {
    Long employeeId = 1L;
    CreateContractDTO dto = new CreateContractDTO(ContractType.CDI, LocalDate.of(2025, 1, 1), null,
        new BigDecimal("5000.00"), 2L);

    doThrow(new NoAccessException("No access to contracts"))
        .when(authorizationService).contractRights(employeeId);

    assertThatThrownBy(() -> contractService.create(dto, employeeId))
        .isInstanceOf(NoAccessException.class);

    verify(contractMapper, never()).toEntity(any());
    verify(contractRepository, never()).save(any());
  }

  @Test
  void findAll_shouldReturnAllContracts() {
    Long employeeId = 1L;

    Contract contract = new Contract();
    contract.setId(5L);

    List<Contract> contracts = List.of(contract);
    List<GetContractDTO> dtos = List.of(
        new GetContractDTO(5L, ContractType.CDI, LocalDate.of(2025, 1, 1), null,
            new BigDecimal("5000.00"), ContractStatus.ACTIVE, 2L, "Ahmed Alami"));

    when(contractRepository.findAll()).thenReturn(contracts);
    when(contractMapper.toGetDtoList(contracts)).thenReturn(dtos);

    List<GetContractDTO> result = contractService.findAll(employeeId);

    assertThat(result).isEqualTo(dtos);
    verify(authorizationService).contractRights(employeeId);
    verify(contractRepository).findAll();
  }

  @Test
  void findById_shouldReturnContract() {
    Long employeeId = 1L;
    Long id = 5L;

    Contract contract = new Contract();
    contract.setId(id);

    GetContractDTO dto = new GetContractDTO(id, ContractType.CDI, LocalDate.of(2025, 1, 1), null,
        new BigDecimal("5000.00"), ContractStatus.ACTIVE, 2L, "Ahmed Alami");

    when(contractRepository.findById(id)).thenReturn(Optional.of(contract));
    when(contractMapper.toGetDto(contract)).thenReturn(dto);

    Optional<GetContractDTO> result = contractService.findById(id, employeeId);

    assertThat(result).contains(dto);
    verify(contractRepository).findById(id);
  }

  @Test
  void findById_shouldReturnEmptyWhenNotFound() {
    Long employeeId = 1L;
    Long id = 5L;

    when(contractRepository.findById(id)).thenReturn(Optional.empty());

    Optional<GetContractDTO> result = contractService.findById(id, employeeId);

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldSaveAndReturnContract() {
    Long employeeId = 1L;
    Long id = 5L;
    Long targetEmployeeId = 2L;
    CreateContractDTO dto = new CreateContractDTO(ContractType.CDD, LocalDate.of(2025, 2, 1), null,
        new BigDecimal("6000.00"), targetEmployeeId);

    Employee employee = new Employee();
    employee.setId(targetEmployeeId);

    Contract contract = new Contract();
    contract.setContractType(ContractType.CDD);
    contract.setStartDate(LocalDate.of(2025, 2, 1));
    contract.setSalaireBrut(new BigDecimal("6000.00"));

    Contract existing = new Contract();
    existing.setId(id);
    existing.setStatus(ContractStatus.ACTIVE);

    Contract saved = new Contract();
    saved.setId(id);
    saved.setContractType(ContractType.CDD);
    saved.setStatus(ContractStatus.ACTIVE);
    saved.setEmployee(employee);

    GetContractDTO resultDto = new GetContractDTO(id, ContractType.CDD, LocalDate.of(2025, 2, 1),
        null, new BigDecimal("6000.00"), ContractStatus.ACTIVE, targetEmployeeId, "Ahmed Alami");

    when(contractMapper.toEntity(dto)).thenReturn(contract);
    when(contractRepository.findById(id)).thenReturn(Optional.of(existing));
    when(employeeRepository.findById(targetEmployeeId)).thenReturn(Optional.of(employee));
    when(contractRepository.save(contract)).thenReturn(saved);
    when(contractMapper.toGetDto(saved)).thenReturn(resultDto);

    GetContractDTO result = contractService.update(id, dto, employeeId);

    assertThat(result).isEqualTo(resultDto);
    assertThat(contract.getId()).isEqualTo(id);
    assertThat(contract.getStatus()).isEqualTo(ContractStatus.ACTIVE);
    verify(contractRepository).save(contract);
  }

  @Test
  void findHistoryByEmployee_shouldReturnContracts() {
    Long requesterId = 1L;
    Long targetEmployeeId = 2L;

    Contract contract = new Contract();
    contract.setId(5L);

    List<Contract> contracts = List.of(contract);
    List<GetContractDTO> dtos = List.of(
        new GetContractDTO(5L, ContractType.CDI, LocalDate.of(2025, 1, 1), null,
            new BigDecimal("5000.00"), ContractStatus.ACTIVE, targetEmployeeId, "Ahmed Alami"));

    when(contractRepository.findByEmployee_IdOrderByStartDateDesc(targetEmployeeId))
        .thenReturn(contracts);
    when(contractMapper.toGetDtoList(contracts)).thenReturn(dtos);

    List<GetContractDTO> result = contractService.findHistoryByEmployee(targetEmployeeId, requesterId);

    assertThat(result).isEqualTo(dtos);
    verify(authorizationService).contractRights(requesterId);
  }

  @Test
  void terminate_shouldTerminateContract() {
    Long employeeId = 1L;
    Long id = 5L;

    Contract contract = new Contract();
    contract.setId(id);
    contract.setStatus(ContractStatus.ACTIVE);

    GetContractDTO dto = new GetContractDTO(id, ContractType.CDI, LocalDate.of(2025, 1, 1),
        LocalDate.now(), new BigDecimal("5000.00"), ContractStatus.TERMINATED, 2L, "Ahmed Alami");

    when(contractRepository.findById(id)).thenReturn(Optional.of(contract));
    when(contractRepository.save(contract)).thenReturn(contract);
    when(contractMapper.toGetDto(contract)).thenReturn(dto);

    Optional<GetContractDTO> result = contractService.terminate(id, employeeId);

    assertThat(result).contains(dto);
    assertThat(contract.getStatus()).isEqualTo(ContractStatus.TERMINATED);
    assertThat(contract.getEndDate()).isEqualTo(LocalDate.now());
  }

  @Test
  void terminate_shouldReturnEmptyWhenNotFound() {
    Long employeeId = 1L;
    Long id = 5L;

    when(contractRepository.findById(id)).thenReturn(Optional.empty());

    Optional<GetContractDTO> result = contractService.terminate(id, employeeId);

    assertThat(result).isEmpty();
  }

  @Test
  void delete_shouldDeleteContract() {
    Long employeeId = 1L;
    Long id = 5L;

    contractService.delete(id, employeeId);

    verify(authorizationService).contractRights(employeeId);
    verify(contractRepository).deleteById(id);
  }
}
