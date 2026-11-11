package ma.atlasrh.atlasrh.leave.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
import ma.atlasrh.atlasrh.leave.entity.LeaveType;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class LeaveRequestRepositoryTest {

  @Autowired
  private LeaveRequestRepository leaveRequestRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Long savedId;
  private Long savedEmployeeId;
  private Long savedManagerId;
  private Long savedLeaveTypeId;

  @BeforeEach
  void setUp() {
    Employee manager = newEmployee("MAT-010", "Marc", "Chef", "marc@atlasrh.ma", Role.MANAGER, null);
    entityManager.persist(manager);

    Employee employee = newEmployee("MAT-001", "Alice", "Martin", "alice@atlasrh.ma", Role.EMPLOYE, manager);
    entityManager.persist(employee);

    LeaveType leaveType = newLeaveType("Conge annuel", 30);
    entityManager.persist(leaveType);

    LeaveRequest leaveRequest = newLeaveRequest(employee, leaveType, LocalDate.of(2024, 6, 1),
        LocalDate.of(2024, 6, 5), 5, "Vacances", LeaveRequestStatus.EN_ATTENTE);
    entityManager.persist(leaveRequest);
    entityManager.flush();
    entityManager.clear();

    savedId = leaveRequest.getId();
    savedEmployeeId = employee.getId();
    savedManagerId = manager.getId();
    savedLeaveTypeId = leaveType.getId();
  }

  private Employee newEmployee(String matricule, String firstName, String lastName, String email,
      Role role, Employee manager) {
    Employee employee = new Employee();
    employee.setMatricule(matricule);
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setEmail(email);
    employee.setPoste("Developpeur");
    employee.setRole(role);
    employee.setHireDate(LocalDate.of(2024, 1, 1));
    employee.setManager(manager);
    return employee;
  }

  private LeaveType newLeaveType(String name, int daysPerYear) {
    LeaveType leaveType = new LeaveType();
    leaveType.setName(name);
    leaveType.setDaysPerYear(daysPerYear);
    return leaveType;
  }

  private LeaveRequest newLeaveRequest(Employee employee, LeaveType leaveType, LocalDate startDate,
      LocalDate endDate, int jourTravail, String reason, LeaveRequestStatus status) {
    LeaveRequest leaveRequest = new LeaveRequest();
    leaveRequest.setEmployee(employee);
    leaveRequest.setLeaveType(leaveType);
    leaveRequest.setStartDate(startDate);
    leaveRequest.setEndDate(endDate);
    leaveRequest.setJourTravail(jourTravail);
    leaveRequest.setReason(reason);
    leaveRequest.setStatus(status);
    return leaveRequest;
  }

  private LeaveRequest persistLeaveRequest(LeaveRequest leaveRequest) {
    entityManager.persist(leaveRequest);
    entityManager.flush();
    entityManager.clear();
    return leaveRequest;
  }

  private Employee findEmployee(Long id) {
    return entityManager.find(Employee.class, id);
  }

  private LeaveType findLeaveType(Long id) {
    return entityManager.find(LeaveType.class, id);
  }

  @Test
  void createLeaveRequestTest() {
    LeaveRequest leaveRequest = newLeaveRequest(findEmployee(savedEmployeeId), findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 3), 3, "Raison personnelle",
        LeaveRequestStatus.EN_ATTENTE);

    LeaveRequest saved = leaveRequestRepository.saveAndFlush(leaveRequest);
    entityManager.clear();

    LeaveRequest found = leaveRequestRepository.findById(saved.getId()).orElseThrow();
    assertThat(saved.getId()).isNotNull();
    assertThat(found.getStartDate()).isEqualTo(LocalDate.of(2024, 7, 1));
    assertThat(found.getEndDate()).isEqualTo(LocalDate.of(2024, 7, 3));
    assertThat(found.getJourTravail()).isEqualTo(3);
    assertThat(found.getReason()).isEqualTo("Raison personnelle");
    assertThat(found.getStatus()).isEqualTo(LeaveRequestStatus.EN_ATTENTE);
  }

  @Test
  void findById_returnsLeaveRequest_whenExists() {
    LeaveRequest found = leaveRequestRepository.findById(savedId).orElseThrow();

    assertThat(found.getStartDate()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(found.getReason()).isEqualTo("Vacances");
    assertThat(found.getStatus()).isEqualTo(LeaveRequestStatus.EN_ATTENTE);
  }

  @Test
  void findAll_returnsAllLeaveRequests() {
    persistLeaveRequest(newLeaveRequest(findEmployee(savedEmployeeId), findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 3), 3, "Autre", LeaveRequestStatus.APPROUVEE));

    List<LeaveRequest> all = leaveRequestRepository.findAll();

    assertThat(all)
        .extracting(LeaveRequest::getReason)
        .containsExactlyInAnyOrder("Vacances", "Autre");
  }

  @Test
  void updateLeaveRequestTest() {
    LeaveRequest leaveRequest = leaveRequestRepository.findById(savedId).orElseThrow();
    leaveRequest.setStatus(LeaveRequestStatus.APPROUVEE);
    leaveRequest.setManagerComment("Bonne vacances");

    leaveRequestRepository.saveAndFlush(leaveRequest);
    entityManager.clear();

    LeaveRequest found = leaveRequestRepository.findById(savedId).orElseThrow();
    assertThat(found.getStatus()).isEqualTo(LeaveRequestStatus.APPROUVEE);
    assertThat(found.getManagerComment()).isEqualTo("Bonne vacances");
  }

  @Test
  void deleteLeaveRequestTest() {
    leaveRequestRepository.deleteById(savedId);
    leaveRequestRepository.flush();
    entityManager.clear();

    assertThat(leaveRequestRepository.findById(savedId)).isEmpty();
  }

  @Test
  void deleteLeaveRequest_onlyRemovesTheTargetOne() {
    persistLeaveRequest(newLeaveRequest(findEmployee(savedEmployeeId), findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 3), 3, "Autre", LeaveRequestStatus.APPROUVEE));

    leaveRequestRepository.deleteById(savedId);
    leaveRequestRepository.flush();
    entityManager.clear();

    assertThat(leaveRequestRepository.findAll())
        .extracting(LeaveRequest::getReason)
        .containsExactly("Autre");
  }

  @Test
  void findByEmployee_Id_returnsRequestsOfEmployee() {
    Employee other = newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.EMPLOYE, null);
    entityManager.persist(other);
    persistLeaveRequest(newLeaveRequest(other, findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 3), 3, "Autre", LeaveRequestStatus.EN_ATTENTE));

    List<LeaveRequest> requests = leaveRequestRepository.findByEmployee_Id(savedEmployeeId);

    assertThat(requests)
        .extracting(LeaveRequest::getReason)
        .containsExactly("Vacances");
  }

  @Test
  void findByEmployee_IdAndStatus_filtersByStatus() {
    persistLeaveRequest(newLeaveRequest(findEmployee(savedEmployeeId), findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 3), 3, "Autre", LeaveRequestStatus.APPROUVEE));

    List<LeaveRequest> pending = leaveRequestRepository.findByEmployee_IdAndStatus(savedEmployeeId,
        LeaveRequestStatus.EN_ATTENTE);
    List<LeaveRequest> approved = leaveRequestRepository.findByEmployee_IdAndStatus(savedEmployeeId,
        LeaveRequestStatus.APPROUVEE);

    assertThat(pending)
        .extracting(LeaveRequest::getReason)
        .containsExactly("Vacances");
    assertThat(approved)
        .extracting(LeaveRequest::getReason)
        .containsExactly("Autre");
  }

  @Test
  void findByEmployee_Manager_IdAndStatus_returnsRequestsOfSubordinates() {
    Employee other = newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma", Role.EMPLOYE, null);
    entityManager.persist(other);
    persistLeaveRequest(newLeaveRequest(other, findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 3), 3, "Autre", LeaveRequestStatus.EN_ATTENTE));

    List<LeaveRequest> requests = leaveRequestRepository.findByEmployee_Manager_IdAndStatus(savedManagerId,
        LeaveRequestStatus.EN_ATTENTE);

    assertThat(requests)
        .extracting(LeaveRequest::getReason)
        .containsExactly("Vacances");
  }

  @Test
  void findByEmployee_IdAndStatusAndStartDateBetween_filtersByDateRange() {
    persistLeaveRequest(newLeaveRequest(findEmployee(savedEmployeeId), findLeaveType(savedLeaveTypeId),
        LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 3), 3, "Aout", LeaveRequestStatus.EN_ATTENTE));

    List<LeaveRequest> requests = leaveRequestRepository.findByEmployee_IdAndStatusAndStartDateBetween(
        savedEmployeeId, LeaveRequestStatus.EN_ATTENTE, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 7, 1));

    assertThat(requests)
        .extracting(LeaveRequest::getReason)
        .containsExactly("Vacances");
  }
}
