package ma.atlasrh.atlasrh.employee.entity;

import java.time.LocalDate;
import java.util.List;

import io.micrometer.common.lang.internal.Contract;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.atlasrh.atlasrh.department.entity.Departement;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;

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

  @ManyToOne
  private Departement departement;

  @OneToMany(mappedBy = "employee")
  private List<Contract> contracts;

  @OneToMany(mappedBy = "employee")
  private List<LeaveRequest> leaveRequests;
}
