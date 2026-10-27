package ma.atlasrh.atlasrh.leave.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;

/**
 * ChangeLeaveStatus
 */
public record ChangeLeaveStatus(@NotNull Long leaveId, @NotNull LeaveRequestStatus status,
    @Size(max = 255) String managerComment) {
}
