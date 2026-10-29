package ma.atlasrh.atlasrh.leave.dto;

import jakarta.validation.constraints.NotNull;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;

/**
 * CancelLeaveRequestDTO
 */
public record CancelLeaveRequestDTO(@NotNull Long leaveId, @NotNull LeaveRequestStatus status) {
}
