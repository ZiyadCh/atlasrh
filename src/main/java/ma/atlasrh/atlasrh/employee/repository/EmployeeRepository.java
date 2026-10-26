package ma.atlasrh.atlasrh.employee.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ma.atlasrh.atlasrh.employee.entity.Employee;

/**
 * EmployeeRepository
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByDepartement_Id(Long departementId);

  List<Employee> findByManager_Id(Long managerId);

  List<Employee> findByDepartement_Id(Long departementId, Sort sort);

  List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrDepartement_NameContainingIgnoreCase(
      String firstName, String lastName, String departementName, Sort sort);
}


