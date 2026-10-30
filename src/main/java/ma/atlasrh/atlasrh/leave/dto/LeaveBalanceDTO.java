package ma.atlasrh.atlasrh.leave.dto;

/**
 * LeaveBalanceDTO
 */
public record LeaveBalanceDTO(
    Long leaveTypeId,
    String leaveTypeName,
    int daysPerYear,
    int daysUsed,
    int daysRemaining
) {
}