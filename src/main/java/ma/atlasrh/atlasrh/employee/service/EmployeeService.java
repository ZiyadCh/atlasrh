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
      employees = employeeRepository.searchByName(search, sort);
    } else {
      employees = employeeRepository.findAll(sort);
    }
    return eMapper.toGetDtoList(employees);
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

  public Page<GetEmployeeDTO> listInPage(int page, int size, Long employeeId) {
    authorizationService.employeeRights(employeeId);
    Pageable pageable = PageRequest.of(page, size);
    return employeeRepository.findAll(pageable).map(eMapper::toGetDto);
  }

  private void setDepartement(Employee employee, Long departementId) {
    if (departementId != null) {
      employee.setDepartement(departementRepository.findById(departementId).orElse(null));
    }
  }

  private void setManager(Employee employee, Long managerId) {
    if (managerId != null) {
      employee.setManager(employeeRepository.findById(managerId).orElse(null));
    }
  }
}
