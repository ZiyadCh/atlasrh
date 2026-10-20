package ma.atlasrh.atlasrh.employee.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
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
import ma.atlasrh.atlasrh.department.entity.Departement;

/**
 * Employee
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String marticule;
  private String firstName;
  private String lastName;
  @Column(unique = true)
  private String email;
  private String poste;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;
  private LocalDate hireDate;

  @ManyToOne()
  private Departement departement;
}
