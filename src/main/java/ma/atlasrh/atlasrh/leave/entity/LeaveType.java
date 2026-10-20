package ma.atlasrh.atlasrh.leave.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LeaveType
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LeaveType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private int daysPerYear;
}
