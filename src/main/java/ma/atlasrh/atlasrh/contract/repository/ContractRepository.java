package ma.atlasrh.atlasrh.contract.repository;

import ma.atlasrh.atlasrh.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ContractRepository
 */
public interface ContractRepository extends JpaRepository<Contract, Long> {
}
