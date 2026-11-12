package ma.atlasrh.atlasrh.departement.repository;

import java.util.List;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * DepartementRepository
 */
public interface DepartementRepository extends JpaRepository<Departement, Long> {
  @EntityGraph(attributePaths = "employees")
  @Query("SELECT d FROM Departement d")
  List<Departement> findAllWithEmployees();
}
