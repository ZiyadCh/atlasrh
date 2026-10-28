package ma.atlasrh.atlasrh.employee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ma.atlasrh.atlasrh.employee.entity.Employee;

/**
 * EmployeeRepository
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByDepartement_Id(Long departementId);

  List<Employee> findByDepartement_Id(Long departementId, Sort sort);

  Page<Employee> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
}
