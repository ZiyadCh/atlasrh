package ma.atlasrh.atlasrh.contract.repository;

import java.util.List;

import ma.atlasrh.atlasrh.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ContractRepository
 */
public interface ContractRepository extends JpaRepository<Contract, Long> {
  List<Contract> findByEmployee_IdOrderByStartDateDesc(Long employeeId);
}
