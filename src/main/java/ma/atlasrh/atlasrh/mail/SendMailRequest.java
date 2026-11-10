package ma.atlasrh.atlasrh.mail;

import jakarta.validation.constraints.NotNull;

/**
 * SendMailRequest
 */
public record SendMailRequest(@NotNull Long employeeId) {
}
