package ma.atlasrh.atlasrh.employee.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.atlasrh.atlasrh.employee.entity.Employee;

/**
 * EmployeeRepository
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByDepartement_Id(Long departementId);

  List<Employee> findByDepartement_Id(Long departementId, Sort sort);

  @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) "
      + "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
  List<Employee> searchByName(@Param("search") String search, Sort sort);
}


