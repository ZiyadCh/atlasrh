package ma.atlasrh.atlasrh.contract.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.atlasrh.atlasrh.employee.entity.Employee;

/**
 * Contract
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Contract {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private ContractType contractType;
  private LocalDate startDate;
  private LocalDate endDate;
  private BigDecimal salaireBrut;

  @ManyToOne
  private Employee employee;
}
