package ma.atlasrh.atlasrh.leave.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLeaveRequest(
    @NotNull @FutureOrPresent LocalDate startDate,
    @NotNull LocalDate endDate,
    @Size(max = 500) String reason,
    @NotNull Long leaveTypeId) {
}
