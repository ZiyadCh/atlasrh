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

  public void departementRights(Employee employee) {
    check(employee.getRole() == Role.RH, "No access to departements");
  }

  public void contractRights(Employee employee) {
    check(employee.getRole() == Role.RH, "No access to contracts");
  }

  public void employeeRights(Employee employee) {
    check(employee.getRole() == Role.RH, "No access to employees");
  }

  public void leaveRequestRights(Employee employee) {
    check(employee.getRole() == Role.MANAGER, "No access to leave requests");
  }

  public void employeeLeaveRights(Employee employee) {
    check(employee.getRole() == Role.EMPLOYE, "No access to employee leaves");
  }

  private void check(boolean allowed, String message) {
    if (!allowed) {
      throw new NoAccessException(message);
    }
  }

}
