package ma.atlasrh.atlasrh.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.departement.repository.DepartementRepository;
import ma.atlasrh.atlasrh.employee.dto.CreateEmployeeDTO;
import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.mapper.EmployeeMapper;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;

/**
 * EmployeeService
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper eMapper;
  private final AuthorizationService authorizationService;
  private final DepartementRepository departementRepository;

  @Transactional
  public Employee create(CreateEmployeeDTO cEmployeeDTO, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    Employee employee = eMapper.toEntity(cEmployeeDTO);
    setDepartement(employee, cEmployeeDTO.departementId());
    setManager(employee, cEmployeeDTO.managerId());
    return employeeRepository.save(employee);
  }

  public List<GetEmployeeDTO> findAll(String search, Long departementId, Sort sort, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    List<Employee> employees;
    if (departementId != null) {
      employees = employeeRepository.findByDepartement_Id(departementId, sort);
    } else if (search != null && !search.isBlank()) {
      employees = employeeRepository.findByFirstNameContainingIgnoreCase(search,
          PageRequest.of(0, Integer.MAX_VALUE, sort)).getContent();
    } else {
      employees = employeeRepository.findAll(sort);
    }
    return eMapper.toGetDtoList(employees);
  }

  public Page<GetEmployeeDTO> findAll(String search, Long departementId, Pageable pageable,
      Long employeeId) {
    authorizationService.employeeRights(employeeId);
    Page<Employee> employees;
    if (departementId != null) {
      employees = employeeRepository.findByDepartement_Id(departementId, pageable);
    } else if (search != null && !search.isBlank()) {
      employees = employeeRepository.findByFirstNameContainingIgnoreCase(search, pageable);
    } else {
      employees = employeeRepository.findAll(pageable);
    }
    return employees.map(eMapper::toGetDto);
  }

  public List<GetEmployeeDTO> getTeam(Long managerId, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    List<Employee> team = employeeRepository.findByManager_Id(managerId, Sort.unsorted());
    return eMapper.toGetDtoList(team);
  }

  public Optional<GetEmployeeDTO> findById(Long id, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    return employeeRepository.findById(id).map(eMapper::toGetDto);
  }

  @Transactional
  public GetEmployeeDTO update(Long id, CreateEmployeeDTO cEmployeeDTO, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    Employee employee = eMapper.toEntity(cEmployeeDTO);
    employee.setId(id);
    setDepartement(employee, cEmployeeDTO.departementId());
    setManager(employee, cEmployeeDTO.managerId());
    return eMapper.toGetDto(employeeRepository.save(employee));
  }

  @Transactional
  public void delete(Long id, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    employeeRepository.deleteById(id);
  }

  public Page<GetEmployeeDTO> listInPage(int page, int size, Long employeeId, String firstName) {
    authorizationService.employeeRights(employeeId);
    Pageable pageable = PageRequest.of(page, size);
    if (firstName != null && !firstName.isBlank()) {
      return employeeRepository.findByFirstNameContainingIgnoreCase(firstName, pageable).map(eMapper::toGetDto);
    }
    return employeeRepository.findAll(pageable).map(eMapper::toGetDto);
  }

  private void setDepartement(Employee employee, Long departementId) {
    if (departementId == null) {
      throw new IllegalArgumentException("Le département est obligatoire");
    }
    employee.setDepartement(departementRepository.findById(departementId)
        .orElseThrow(() -> new IllegalArgumentException("Département introuvable: " + departementId)));
  }

  private void setManager(Employee employee, Long managerId) {
    if (managerId != null) {
      employee.setManager(employeeRepository.findById(managerId).orElse(null));
    }
  }
}
