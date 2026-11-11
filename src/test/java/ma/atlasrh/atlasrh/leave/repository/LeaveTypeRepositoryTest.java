package ma.atlasrh.atlasrh.leave.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ma.atlasrh.atlasrh.leave.entity.LeaveType;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class LeaveTypeRepositoryTest {

  @Autowired
  private LeaveTypeRepository leaveTypeRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Long savedId;

  @BeforeEach
  void setUp() {
    LeaveType leaveType = newLeaveType("Conge annuel", 30);
    entityManager.persist(leaveType);
    entityManager.flush();
    entityManager.clear();
    savedId = leaveType.getId();
  }

  private LeaveType newLeaveType(String name, int daysPerYear) {
    LeaveType leaveType = new LeaveType();
    leaveType.setName(name);
    leaveType.setDaysPerYear(daysPerYear);
    return leaveType;
  }

  @Test
  void createLeaveTypeTest() {
    LeaveType leaveType = newLeaveType("Conge maladie", 10);

    LeaveType saved = leaveTypeRepository.saveAndFlush(leaveType);
    entityManager.clear();

    LeaveType found = leaveTypeRepository.findById(saved.getId()).orElseThrow();
    assertThat(saved.getId()).isNotNull();
    assertThat(found.getName()).isEqualTo("Conge maladie");
    assertThat(found.getDaysPerYear()).isEqualTo(10);
  }

  @Test
  void findById_returnsLeaveType_whenExists() {
    LeaveType found = leaveTypeRepository.findById(savedId).orElseThrow();

    assertThat(found.getName()).isEqualTo("Conge annuel");
    assertThat(found.getDaysPerYear()).isEqualTo(30);
  }

  @Test
  void findAll_returnsAllLeaveTypes() {
    entityManager.persist(newLeaveType("Conge maladie", 10));
    entityManager.flush();
    entityManager.clear();

    List<LeaveType> all = leaveTypeRepository.findAll();

    assertThat(all)
        .extracting(LeaveType::getName)
        .containsExactlyInAnyOrder("Conge annuel", "Conge maladie");
  }

  @Test
  void updateLeaveTypeTest() {
    LeaveType leaveType = leaveTypeRepository.findById(savedId).orElseThrow();
    leaveType.setName("Conge annuel paye");
    leaveType.setDaysPerYear(25);

    leaveTypeRepository.saveAndFlush(leaveType);
    entityManager.clear();

    LeaveType found = leaveTypeRepository.findById(savedId).orElseThrow();
    assertThat(found.getName()).isEqualTo("Conge annuel paye");
    assertThat(found.getDaysPerYear()).isEqualTo(25);
  }

  @Test
  void deleteLeaveTypeTest() {
    leaveTypeRepository.deleteById(savedId);
    leaveTypeRepository.flush();
    entityManager.clear();

    assertThat(leaveTypeRepository.findById(savedId)).isEmpty();
  }

  @Test
  void deleteLeaveType_onlyRemovesTheTargetOne() {
    entityManager.persist(newLeaveType("Conge maladie", 10));
    entityManager.flush();
    entityManager.clear();

    leaveTypeRepository.deleteById(savedId);
    leaveTypeRepository.flush();
    entityManager.clear();

    assertThat(leaveTypeRepository.findAll())
        .extracting(LeaveType::getName)
        .containsExactly("Conge maladie");
  }
}
