package ma.atlasrh.atlasrh.department.repository;

import ma.atlasrh.atlasrh.department.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DepartementRepository
 */
public interface DepartementRepository extends JpaRepository<Departement, Long> {
}
