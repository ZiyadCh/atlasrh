package ma.atlasrh.atlasrh.departement.repository;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DepartementRepository
 */
public interface DepartementRepository extends JpaRepository<Departement, Long> {
}
