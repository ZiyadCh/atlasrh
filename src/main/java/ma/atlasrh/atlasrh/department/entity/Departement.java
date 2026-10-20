package ma.atlasrh.atlasrh.department.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.atlasrh.atlasrh.employee.entity.Employee;

/**
 * Departement
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Departement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String name;
  private String description;
  @OneToMany(mappedBy = "department")
  private List<Employee> employees;
}
