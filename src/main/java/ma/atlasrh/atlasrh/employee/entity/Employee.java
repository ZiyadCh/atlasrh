package ma.atlasrh.atlasrh.employee.entity;

import java.time.LocalDate;
import java.util.List;

import ma.atlasrh.atlasrh.contract.entity.Contract;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;

/**
 * Employee
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String matricule;
  private String firstName;
  private String lastName;
  @Column(unique = true)
  private String email;
  private String poste;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;
  private LocalDate hireDate;

  @ManyToOne(optional = false)
  private Departement departement;

  @ManyToOne(optional = true)
  private Employee manager;

  @OneToMany(mappedBy = "employee")
  private List<Contract> contracts;

  @OneToMany(mappedBy = "employee")
  private List<LeaveRequest> leaveRequests;
}
