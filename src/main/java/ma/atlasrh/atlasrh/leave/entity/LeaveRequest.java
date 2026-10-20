package ma.atlasrh.atlasrh.leave.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LeaveRequest
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LeaveRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private LocalDate startDate;
  private LocalDate endDate;
  private int jourTravail;
  private String reason;
  @Enumerated(EnumType.STRING)
  private LeaveRequestStatus status;
  private String managerComment;
}
