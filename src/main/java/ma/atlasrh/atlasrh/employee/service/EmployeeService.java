package ma.atlasrh.atlasrh.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;

/**
 * EmployeeService
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;

  public Employee create(Employee employee) {
    return employeeRepository.save(employee);
  }

  public List<Employee> findAll() {
    return employeeRepository.findAll();
  }

  public Optional<Employee> findById(Long id) {
    return employeeRepository.findById(id);
  }

  public Employee update(Long id, Employee employee) {
    employee.setId(id);
    return employeeRepository.save(employee);
  }

  public void delete(Long id) {
    employeeRepository.deleteById(id);
  }
}
