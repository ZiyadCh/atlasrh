package ma.atlasrh.atlasrh.employee.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;

/**
 * Unit tests for {@link AuthorizationService}: every right is granted to the expected role and denied otherwise.
 */
@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private AuthorizationService authorizationService;

  @Test
  void departementRights_shouldAllowRh() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.RH)));

    authorizationService.departementRights(1L);
  }

  @Test
  void departementRights_shouldDenyNonRh() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.EMPLOYE)));

    assertThatThrownBy(() -> authorizationService.departementRights(1L))
        .isInstanceOf(NoAccessException.class)
        .hasMessage("No access to departements");
  }

  @Test
  void contractRights_shouldAllowRh() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.RH)));

    authorizationService.contractRights(1L);
  }

  @Test
  void contractRights_shouldDenyNonRh() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.MANAGER)));

    assertThatThrownBy(() -> authorizationService.contractRights(1L))
        .isInstanceOf(NoAccessException.class)
        .hasMessage("No access to contracts");
  }

  @Test
  void employeeRights_shouldAllowRh() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.RH)));

    authorizationService.employeeRights(1L);
  }

  @Test
  void employeeRights_shouldDenyNonRh() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.EMPLOYE)));

    assertThatThrownBy(() -> authorizationService.employeeRights(1L))
        .isInstanceOf(NoAccessException.class)
        .hasMessage("No access to employees");
  }

  @Test
  void leaveRequestRights_shouldAllowManager() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.MANAGER)));

    authorizationService.leaveRequestRights(1L);
  }

  @Test
  void leaveRequestRights_shouldDenyNonManager() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.RH)));

    assertThatThrownBy(() -> authorizationService.leaveRequestRights(1L))
        .isInstanceOf(NoAccessException.class)
        .hasMessage("No access to leave requests");
  }

  @Test
  void employeeLeaveRights_shouldAllowEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.EMPLOYE)));

    authorizationService.employeeLeaveRights(1L);
  }

  @Test
  void employeeLeaveRights_shouldDenyNonEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.RH)));

    assertThatThrownBy(() -> authorizationService.employeeLeaveRights(1L))
        .isInstanceOf(NoAccessException.class)
        .hasMessage("No access to employee leaves");
  }

  @Test
  void rights_shouldThrowWhenEmployeeNotFound() {
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorizationService.employeeRights(99L))
        .isInstanceOf(NoAccessException.class)
        .hasMessage("Employee not found");
  }

  private Employee employee(Long id, Role role) {
    Employee employee = new Employee();
    employee.setId(id);
    employee.setRole(role);
    return employee;
  }
}
