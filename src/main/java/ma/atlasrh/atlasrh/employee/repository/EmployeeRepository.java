package ma.atlasrh.atlasrh.employee.repository;

import ma.atlasrh.atlasrh.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * EmployeeRepository
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByDepartement_Id(Long departementId);
}
