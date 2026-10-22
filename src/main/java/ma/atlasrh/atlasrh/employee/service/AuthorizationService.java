package ma.atlasrh.atlasrh.employee.service;

import org.springframework.stereotype.Service;

import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
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

  public boolean departementRights(Employee employee) {
    return employee.getRole() == Role.RH;
  }

  public boolean contractRights(Employee employee) {
    return employee.getRole() == Role.RH;
  }

  public boolean employeeRights(Employee employee) {
    return employee.getRole() == Role.RH;
  }

  public boolean leaveRequestRights(Employee employee) {
    return employee.getRole() == Role.MANAGER;
  }

  public boolean employeeLeaveRights(Employee employee) {
    return employee.getRole() == Role.EMPLOYE;
  }

}
