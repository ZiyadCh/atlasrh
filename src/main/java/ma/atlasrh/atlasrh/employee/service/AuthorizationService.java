package ma.atlasrh.atlasrh.employee.service;

import org.springframework.stereotype.Service;

import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;

/**
 * AuthorizationService
 */
@Service
public class AuthorizationService {
  private final EmployeeRepository employeeRepository;

  public AuthorizationService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  public void departementRights(Long id) {
    check(getEmployee(id).getRole() == Role.RH, "No access to departements");
  }

  public void contractRights(Long id) {
    check(getEmployee(id).getRole() == Role.RH, "No access to contracts");
  }

  public void employeeRights(Long id) {
    check(getEmployee(id).getRole() == Role.RH, "No access to employees");
  }

  public void leaveRequestRights(Long id) {
    check(getEmployee(id).getRole() == Role.MANAGER, "No access to leave requests");
  }

  public void employeeLeaveRights(Long id) {
    check(getEmployee(id).getRole() == Role.EMPLOYE, "No access to employee leaves");
  }

  private Employee getEmployee(Long id) {
    return employeeRepository.findById(id)
        .orElseThrow(() -> new NoAccessException("Employee not found"));
  }

  private void check(boolean allowed, String message) {
    if (!allowed) {
      throw new NoAccessException(message);
    }
  }

}
