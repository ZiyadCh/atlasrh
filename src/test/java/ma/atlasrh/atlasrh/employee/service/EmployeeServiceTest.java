package ma.atlasrh.atlasrh.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.repository.DepartementRepository;
import ma.atlasrh.atlasrh.employee.dto.CreateEmployeeDTO;
import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.mapper.EmployeeMapper;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;

/**
 * EmployeeServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

  @Mock
  EmployeeRepository employeeRepository;
  @Mock
  EmployeeMapper employeeMapper;
  @Mock
  AuthorizationService authorizationService;
  @Mock
  DepartementRepository departementRepository;
  @InjectMocks
  EmployeeService employeeService;

  @Test
  void create_shouldSaveAndReturnEmployee() {
    Long employeeId = 1L;
    Long departementId = 3L;
    Long managerId = 4L;
    CreateEmployeeDTO dto = new CreateEmployeeDTO("MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), departementId, managerId);

    Employee employee = new Employee();
    employee.setMatricule("MAT001");

    Departement departement = new Departement();
    departement.setId(departementId);

    Employee manager = new Employee();
    manager.setId(managerId);

    Employee saved = new Employee();
    saved.setId(2L);
    saved.setMatricule("MAT001");

    when(employeeMapper.toEntity(dto)).thenReturn(employee);
    when(departementRepository.findById(departementId)).thenReturn(Optional.of(departement));
    when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
    when(employeeRepository.save(employee)).thenReturn(saved);

    Employee result = employeeService.create(dto, employeeId);

    assertThat(result).isEqualTo(saved);
    assertThat(result.getId()).isEqualTo(2L);
    assertThat(employee.getDepartement()).isEqualTo(departement);
    assertThat(employee.getManager()).isEqualTo(manager);
    verify(authorizationService).employeeRights(employeeId);
    verify(employeeRepository).save(employee);
  }

  @Test
  void create_shouldThrowWhenNoAccess() {
    Long employeeId = 1L;
    CreateEmployeeDTO dto = new CreateEmployeeDTO("MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), null, null);

    doThrow(new NoAccessException("No access to employees"))
        .when(authorizationService).employeeRights(employeeId);

    assertThatThrownBy(() -> employeeService.create(dto, employeeId))
        .isInstanceOf(NoAccessException.class);

    verify(employeeMapper, never()).toEntity(any());
    verify(employeeRepository, never()).save(any());
  }

  @Test
  void findAll_shouldReturnPageWhenNoFilters() {
    Long employeeId = 1L;
    Pageable pageable = PageRequest.of(0, 20);

    Employee employee = new Employee();
    employee.setId(2L);

    GetEmployeeDTO dto = new GetEmployeeDTO(2L, "MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", null, null, "1 an");

    Page<Employee> page = new PageImpl<>(List.of(employee));

    when(employeeRepository.findAll(pageable)).thenReturn(page);
    when(employeeMapper.toGetDto(employee)).thenReturn(dto);

    Page<GetEmployeeDTO> result = employeeService.findAll(null, null, pageable, employeeId);

    assertThat(result.getContent()).containsExactly(dto);
    verify(authorizationService).employeeRights(employeeId);
  }

  @Test
  void findAll_shouldFilterByDepartement() {
    Long employeeId = 1L;
    Long departementId = 3L;
    Pageable pageable = PageRequest.of(0, 20);

    Employee employee = new Employee();
    employee.setId(2L);

    GetEmployeeDTO dto = new GetEmployeeDTO(2L, "MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), departementId, "IT", null, null,
        "1 an");

    Page<Employee> page = new PageImpl<>(List.of(employee));

    when(employeeRepository.findByDepartement_Id(departementId, pageable)).thenReturn(page);
    when(employeeMapper.toGetDto(employee)).thenReturn(dto);

    Page<GetEmployeeDTO> result = employeeService.findAll(null, departementId, pageable, employeeId);

    assertThat(result.getContent()).containsExactly(dto);
  }

  @Test
  void findAll_shouldSearchByFirstName() {
    Long employeeId = 1L;
    String search = "Ahmed";
    Pageable pageable = PageRequest.of(0, 20);

    Employee employee = new Employee();
    employee.setId(2L);

    GetEmployeeDTO dto = new GetEmployeeDTO(2L, "MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", null, null, "1 an");

    Page<Employee> page = new PageImpl<>(List.of(employee));

    when(employeeRepository.findByFirstNameContainingIgnoreCase(search, pageable)).thenReturn(page);
    when(employeeMapper.toGetDto(employee)).thenReturn(dto);

    Page<GetEmployeeDTO> result = employeeService.findAll(search, null, pageable, employeeId);

    assertThat(result.getContent()).containsExactly(dto);
  }

  @Test
  void getTeam_shouldReturnTeam() {
    Long employeeId = 1L;
    Long managerId = 2L;

    Employee employee = new Employee();
    employee.setId(3L);

    GetEmployeeDTO dto = new GetEmployeeDTO(3L, "MAT002", "Sara", "Bennani", "sara@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", managerId, "Ahmed Alami",
        "1 an");

    List<Employee> team = List.of(employee);

    when(employeeRepository.findByManager_Id(managerId, Sort.unsorted())).thenReturn(team);
    when(employeeMapper.toGetDtoList(team)).thenReturn(List.of(dto));

    List<GetEmployeeDTO> result = employeeService.getTeam(managerId, employeeId);

    assertThat(result).containsExactly(dto);
    verify(authorizationService).employeeRights(employeeId);
  }

  @Test
  void findById_shouldReturnEmployee() {
    Long employeeId = 1L;
    Long id = 2L;

    Employee employee = new Employee();
    employee.setId(id);

    GetEmployeeDTO dto = new GetEmployeeDTO(id, "MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", null, null, "1 an");

    when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
    when(employeeMapper.toGetDto(employee)).thenReturn(dto);

    Optional<GetEmployeeDTO> result = employeeService.findById(id, employeeId);

    assertThat(result).contains(dto);
  }

  @Test
  void findById_shouldReturnEmptyWhenNotFound() {
    Long employeeId = 1L;
    Long id = 2L;

    when(employeeRepository.findById(id)).thenReturn(Optional.empty());

    Optional<GetEmployeeDTO> result = employeeService.findById(id, employeeId);

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldSaveAndReturnEmployee() {
    Long employeeId = 1L;
    Long id = 2L;
    Long departementId = 3L;
    Long managerId = 4L;
    CreateEmployeeDTO dto = new CreateEmployeeDTO("MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Manager", Role.MANAGER, LocalDate.of(2025, 1, 1), departementId, managerId);

    Employee employee = new Employee();
    employee.setMatricule("MAT001");

    Departement departement = new Departement();
    departement.setId(departementId);

    Employee manager = new Employee();
    manager.setId(managerId);

    Employee saved = new Employee();
    saved.setId(id);
    saved.setMatricule("MAT001");

    GetEmployeeDTO resultDto = new GetEmployeeDTO(id, "MAT001", "Ahmed", "Alami",
        "ahmed@atlas.ma", "Manager", Role.MANAGER, LocalDate.of(2025, 1, 1), departementId, "IT",
        managerId, "Ahmed Alami", "1 an");

    when(employeeMapper.toEntity(dto)).thenReturn(employee);
    when(departementRepository.findById(departementId)).thenReturn(Optional.of(departement));
    when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
    when(employeeRepository.save(employee)).thenReturn(saved);
    when(employeeMapper.toGetDto(saved)).thenReturn(resultDto);

    GetEmployeeDTO result = employeeService.update(id, dto, employeeId);

    assertThat(result).isEqualTo(resultDto);
    assertThat(employee.getId()).isEqualTo(id);
    assertThat(employee.getDepartement()).isEqualTo(departement);
    assertThat(employee.getManager()).isEqualTo(manager);
    verify(employeeRepository).save(employee);
  }

  @Test
  void delete_shouldDeleteEmployee() {
    Long employeeId = 1L;
    Long id = 2L;

    employeeService.delete(id, employeeId);

    verify(authorizationService).employeeRights(employeeId);
    verify(employeeRepository).deleteById(id);
  }
}
