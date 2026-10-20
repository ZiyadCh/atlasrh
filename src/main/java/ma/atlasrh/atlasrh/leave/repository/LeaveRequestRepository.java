package ma.atlasrh.atlasrh.leave.repository;

import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LeaveRequestRepository
 */
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
}
