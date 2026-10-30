package ma.atlasrh.atlasrh.leave.repository;

import java.time.LocalDate;
import java.util.List;

import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LeaveRequestRepository
 */
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

  List<LeaveRequest> findByEmployee_Id(Long employeeId);

  List<LeaveRequest> findByEmployee_IdAndStatus(Long employeeId, LeaveRequestStatus status);

  List<LeaveRequest> findByEmployee_Manager_IdAndStatus(Long managerId, LeaveRequestStatus status);

  List<LeaveRequest> findByEmployee_IdAndStatusAndStartDateBetween(
      Long employeeId, LeaveRequestStatus status, LocalDate start, LocalDate end);
}
