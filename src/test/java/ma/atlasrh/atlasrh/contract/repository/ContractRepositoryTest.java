package ma.atlasrh.atlasrh.contract.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ma.atlasrh.atlasrh.contract.entity.Contract;
import ma.atlasrh.atlasrh.contract.entity.ContractStatus;
import ma.atlasrh.atlasrh.contract.entity.ContractType;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class ContractRepositoryTest {

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Long savedId;
  private Long savedEmployeeId;

  @BeforeEach
  void setUp() {
    Employee employee = newEmployee("MAT-001", "Alice", "Martin", "alice@atlasrh.ma");
    entityManager.persist(employee);

    Contract contract = newContract(employee, ContractType.CDI, LocalDate.of(2024, 1, 1),
        LocalDate.of(2025, 1, 1), new BigDecimal("5000.00"), ContractStatus.ACTIVE);
    entityManager.persist(contract);
    entityManager.flush();
    entityManager.clear();

    savedId = contract.getId();
    savedEmployeeId = employee.getId();
  }

  private Employee newEmployee(String matricule, String firstName, String lastName, String email) {
    Employee employee = new Employee();
    employee.setMatricule(matricule);
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setEmail(email);
    employee.setPoste("Developpeur");
    employee.setRole(Role.EMPLOYE);
    employee.setHireDate(LocalDate.of(2024, 1, 1));
    return employee;
  }

  private Contract newContract(Employee employee, ContractType type, LocalDate startDate,
      LocalDate endDate, BigDecimal salaireBrut, ContractStatus status) {
    Contract contract = new Contract();
    contract.setEmployee(employee);
    contract.setContractType(type);
    contract.setStartDate(startDate);
    contract.setEndDate(endDate);
    contract.setSalaireBrut(salaireBrut);
    contract.setStatus(status);
    return contract;
  }

  private Contract persistContract(Contract contract) {
    entityManager.persist(contract);
    entityManager.flush();
    entityManager.clear();
    return contract;
  }

  @Test
  void createContractTest() {
    Employee employee = entityManager.find(Employee.class, savedEmployeeId);

    Contract contract = newContract(employee, ContractType.CDD, LocalDate.of(2024, 3, 1),
        LocalDate.of(2024, 9, 1), new BigDecimal("3500.00"), ContractStatus.ACTIVE);

    Contract saved = contractRepository.saveAndFlush(contract);
    entityManager.clear();

    Contract found = contractRepository.findById(saved.getId()).orElseThrow();
    assertThat(saved.getId()).isNotNull();
    assertThat(found.getContractType()).isEqualTo(ContractType.CDD);
    assertThat(found.getStartDate()).isEqualTo(LocalDate.of(2024, 3, 1));
    assertThat(found.getEndDate()).isEqualTo(LocalDate.of(2024, 9, 1));
    assertThat(found.getSalaireBrut()).isEqualByComparingTo("3500.00");
    assertThat(found.getStatus()).isEqualTo(ContractStatus.ACTIVE);
  }

  @Test
  void findById_returnsContract_whenExists() {
    Contract found = contractRepository.findById(savedId).orElseThrow();

    assertThat(found.getContractType()).isEqualTo(ContractType.CDI);
    assertThat(found.getSalaireBrut()).isEqualByComparingTo("5000.00");
    assertThat(found.getStatus()).isEqualTo(ContractStatus.ACTIVE);
  }

  @Test
  void findAll_returnsAllContracts() {
    Employee employee = entityManager.find(Employee.class, savedEmployeeId);
    persistContract(newContract(employee, ContractType.STAGE, LocalDate.of(2024, 2, 1),
        LocalDate.of(2024, 8, 1), new BigDecimal("1500.00"), ContractStatus.ACTIVE));

    List<Contract> all = contractRepository.findAll();

    assertThat(all)
        .extracting(Contract::getContractType)
        .containsExactlyInAnyOrder(ContractType.CDI, ContractType.STAGE);
  }

  @Test
  void updateContractTest() {
    Contract contract = contractRepository.findById(savedId).orElseThrow();
    contract.setSalaireBrut(new BigDecimal("5500.00"));
    contract.setStatus(ContractStatus.TERMINATED);

    contractRepository.saveAndFlush(contract);
    entityManager.clear();

    Contract found = contractRepository.findById(savedId).orElseThrow();
    assertThat(found.getSalaireBrut()).isEqualByComparingTo("5500.00");
    assertThat(found.getStatus()).isEqualTo(ContractStatus.TERMINATED);
  }

  @Test
  void deleteContractTest() {
    contractRepository.deleteById(savedId);
    contractRepository.flush();
    entityManager.clear();

    assertThat(contractRepository.findById(savedId)).isEmpty();
  }

  @Test
  void deleteContract_onlyRemovesTheTargetOne() {
    Employee employee = entityManager.find(Employee.class, savedEmployeeId);
    persistContract(newContract(employee, ContractType.STAGE, LocalDate.of(2024, 2, 1),
        LocalDate.of(2024, 8, 1), new BigDecimal("1500.00"), ContractStatus.ACTIVE));

    contractRepository.deleteById(savedId);
    contractRepository.flush();
    entityManager.clear();

    assertThat(contractRepository.findAll())
        .extracting(Contract::getContractType)
        .containsExactly(ContractType.STAGE);
  }

  @Test
  void findByEmployee_IdOrderByStartDateDesc_returnsContractsNewestFirst() {
    Employee employee = entityManager.find(Employee.class, savedEmployeeId);
    Employee other = persistEmployee(newEmployee("MAT-002", "Bob", "Durand", "bob@atlasrh.ma"));

    persistContract(newContract(employee, ContractType.CDI, LocalDate.of(2022, 1, 1),
        LocalDate.of(2023, 1, 1), new BigDecimal("4000.00"), ContractStatus.TERMINATED));
    persistContract(newContract(employee, ContractType.CDI, LocalDate.of(2023, 1, 1),
        LocalDate.of(2024, 1, 1), new BigDecimal("4500.00"), ContractStatus.TERMINATED));
    persistContract(newContract(other, ContractType.CDI, LocalDate.of(2025, 1, 1),
        LocalDate.of(2026, 1, 1), new BigDecimal("6000.00"), ContractStatus.ACTIVE));

    List<Contract> contracts = contractRepository.findByEmployee_IdOrderByStartDateDesc(savedEmployeeId);

    assertThat(contracts)
        .extracting(Contract::getStartDate)
        .containsExactly(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2022, 1, 1));
  }

  private Employee persistEmployee(Employee employee) {
    entityManager.persist(employee);
    entityManager.flush();
    return employee;
  }
}
