package ma.atlasrh.atlasrh.leave.repository;

import ma.atlasrh.atlasrh.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LeaveTypeRepository
 */
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}
