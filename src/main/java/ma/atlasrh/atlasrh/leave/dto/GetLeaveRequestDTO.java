package ma.atlasrh.atlasrh.leave.dto;

import java.time.LocalDate;

import ma.atlasrh.atlasrh.employee.entity.Employee;
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
    Long employee) {
}
