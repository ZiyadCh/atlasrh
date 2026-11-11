package ma.atlasrh.atlasrh.employee.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Long savedId;
  private Long savedDepartementId;

  @BeforeEach
  void setUp() {
    Departement departement = newDepartement("Informatique", "Equipe IT");
    entityManager.persist(departement);

    Employee employee = newEmployee("MAT-001", "Alice", "Martin", "alice@atlasrh.ma", Role.EMPLOYE,
        departement, null);
    entityManager.persist(employee);
    entityManager.flush();
    entityManager.clear();

    savedId = employee.getId();
    savedDepartementId = departement.getId();
  }

  private Departement newDepartement(String name, String description) {
    Departement departement = new Departement();
    departement.setName(name);
    departement.setDescription(description);
    return departement;
  }

  private Employee newEmployee(String matricule, String firstName, String lastName, String email,
      Role role, Departement departement, Employee manager) {
    Employee employee = new Employee();
    employee.setMatricule(matricule);
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setEmail(email);
    employee.setPoste("Developpeur");
    employee.setRole(role);
    employee.setHireDate(LocalDate.of(2024, 1, 1));
    employee.setDepartement(departement);
    employee.setManager(manager);
    return employee;
  }

  private Employee persistEmployee(Employee employee) {
    entityManager.persist(employee);
    entityManager.flush();
    entityManager.clear();
    return employee;
  }

  @Test
  void createEmployeeTest() {
    Employee employee = newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.MANAGER, null, null);

    Employee saved = employeeRepository.saveAndFlush(employee);
    entityManager.clear();

    Employee found = employeeRepository.findById(saved.getId()).orElseThrow();
    assertThat(saved.getId()).isNotNull();
    assertThat(found.getMatricule()).isEqualTo("MAT-002");
    assertThat(found.getFirstName()).isEqualTo("Bob");
    assertThat(found.getLastName()).isEqualTo("Durand");
    assertThat(found.getEmail()).isEqualTo("bob@atlasrh.ma");
    assertThat(found.getRole()).isEqualTo(Role.MANAGER);
  }

  @Test
  void findById_returnsEmployee_whenExists() {
    Employee found = employeeRepository.findById(savedId).orElseThrow();

    assertThat(found.getMatricule()).isEqualTo("MAT-001");
    assertThat(found.getFirstName()).isEqualTo("Alice");
    assertThat(found.getEmail()).isEqualTo("alice@atlasrh.ma");
  }

  @Test
  void findAll_returnsAllEmployees() {
    persistEmployee(newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.EMPLOYE, null, null));

    List<Employee> all = employeeRepository.findAll();

    assertThat(all)
        .extracting(Employee::getMatricule)
        .containsExactlyInAnyOrder("MAT-001", "MAT-002");
  }

  @Test
  void updateEmployeeTest() {
    Employee employee = employeeRepository.findById(savedId).orElseThrow();
    employee.setFirstName("Alicia");
    employee.setPoste("Lead Developpeur");

    employeeRepository.saveAndFlush(employee);
    entityManager.clear();

    Employee found = employeeRepository.findById(savedId).orElseThrow();
    assertThat(found.getFirstName()).isEqualTo("Alicia");
    assertThat(found.getPoste()).isEqualTo("Lead Developpeur");
  }

  @Test
  void deleteEmployeeTest() {
    employeeRepository.deleteById(savedId);
    employeeRepository.flush();
    entityManager.clear();

    assertThat(employeeRepository.findById(savedId)).isEmpty();
  }

  @Test
  void deleteEmployee_onlyRemovesTheTargetOne() {
    persistEmployee(newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.EMPLOYE, null, null));

    employeeRepository.deleteById(savedId);
    employeeRepository.flush();
    entityManager.clear();

    assertThat(employeeRepository.findAll())
        .extracting(Employee::getMatricule)
        .containsExactly("MAT-002");
  }

  @Test
  void existsByDepartement_Id_returnsTrue_whenEmployeeExists() {
    assertThat(employeeRepository.existsByDepartement_Id(savedDepartementId)).isTrue();
  }

  @Test
  void existsByDepartement_Id_returnsFalse_whenNoEmployeeExists() {
    assertThat(employeeRepository.existsByDepartement_Id(999L)).isFalse();
  }

  @Test
  void findByDepartement_Id_returnsEmployeesOfDepartement() {
    persistEmployee(newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.EMPLOYE, null, null));

    List<Employee> employees = employeeRepository.findByDepartement_Id(savedDepartementId,
        Sort.by(Sort.Direction.ASC, "firstName"));

    assertThat(employees)
        .extracting(Employee::getMatricule)
        .containsExactly("MAT-001");
  }

  @Test
  void findByDepartement_Id_withPageable_returnsPage() {
    persistEmployee(newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.EMPLOYE, null, null));

    Page<Employee> page = employeeRepository.findByDepartement_Id(savedDepartementId, PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent())
        .extracting(Employee::getMatricule)
        .containsExactly("MAT-001");
  }

  @Test
  void findByFirstNameContainingIgnoreCase_returnsMatchingEmployees() {
    persistEmployee(newEmployee("MAT-002", "Alicia", "Durand", "alicia@atlasrh.ma", Role.EMPLOYE, null, null));

    Page<Employee> page = employeeRepository.findByFirstNameContainingIgnoreCase("ALIC",
        PageRequest.of(0, 10));

    assertThat(page.getContent())
        .extracting(Employee::getMatricule)
        .containsExactlyInAnyOrder("MAT-001", "MAT-002");
  }

  @Test
  void findByManager_Id_returnsSubordinates() {
    Employee manager = persistEmployee(
        newEmployee("MAT-010", "Marc", "Chef", "marc@atlasrh.ma", Role.MANAGER, null, null));

    persistEmployee(newEmployee("MAT-011", "Sofia", "Bennani", "sofia@atlasrh.ma", Role.EMPLOYE, null, manager));

    List<Employee> subordinates = employeeRepository.findByManager_Id(manager.getId(),
        Sort.by(Sort.Direction.ASC, "firstName"));

    assertThat(subordinates)
        .extracting(Employee::getMatricule)
        .containsExactly("MAT-011");
  }
}
