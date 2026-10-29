package ma.atlasrh.atlasrh.leave.dto;

import java.time.LocalDate;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;

/**
 * GetLeaveRequestDTO
 */
public record GetLeaveRequestDTO(
    Long id,
    LocalDate startDate,
    LocalDate endDate,
    int jourTravail,
    String reason,
    LeaveRequestStatus status,
    String managerComment,
    String employeeMatricule,
    String employeeFirstName,
    String employeeLastName,
    String leaveTypeName) {
}
