package ma.atlasrh.atlasrh.departement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ma.atlasrh.atlasrh.departement.entity.Departement;

@DataJpaTest
class DepartementRepositoryTest {

  @Autowired
  private DepartementRepository departementRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Long savedId;

  @BeforeEach
  void setUp() {
    Departement departement = new Departement();
    departement.setName("Informatique");
    departement.setDescription("Equipe IT");

    entityManager.persist(departement);
    entityManager.flush();
    entityManager.clear();
    savedId = departement.getId();
  }

  @Test
  void createDepartementTest() {
    Departement departement = new Departement();
    departement.setName("testName");
    departement.setDescription("testDescription");

    Departement saved = departementRepository.saveAndFlush(departement);
    entityManager.clear();

    Departement found = departementRepository.findById(saved.getId()).orElseThrow();
    assertThat(saved.getId()).isNotNull();
    assertThat(found.getName()).isEqualTo("testName");
    assertThat(found.getDescription()).isEqualTo("testDescription");
  }

  @Test
  void findById_returnsDepartement_whenExists() {
    Departement found = departementRepository.findById(savedId).orElseThrow();

    assertThat(found.getName()).isEqualTo("Informatique");
    assertThat(found.getDescription()).isEqualTo("Equipe IT");
  }

  @Test
  void findAll_returnsAllDepartements() {
    Departement finance = new Departement();
    finance.setName("Finance");
    finance.setDescription("Equipe finance");
    entityManager.persist(finance);
    entityManager.flush();
    entityManager.clear();

    List<Departement> all = departementRepository.findAll();

    assertThat(all)
        .extracting(Departement::getName)
        .containsExactlyInAnyOrder("Informatique", "Finance");
  }

  @Test
  void updateDepartementTest() {
    Departement departement = departementRepository.findById(savedId).orElseThrow();
    departement.setName("Informatique v2");
    departement.setDescription("Nouvelle description");

    departementRepository.saveAndFlush(departement);
    entityManager.clear();

    Departement found = departementRepository.findById(savedId).orElseThrow();
    assertThat(found.getName()).isEqualTo("Informatique v2");
    assertThat(found.getDescription()).isEqualTo("Nouvelle description");
  }

  @Test
  void deleteDepartementTest() {
    departementRepository.deleteById(savedId);
    departementRepository.flush();
    entityManager.clear();

    assertThat(departementRepository.findById(savedId)).isEmpty();
  }

  @Test
  void deleteDepartement_onlyRemovesTheTargetOne() {
    Departement finance = new Departement();
    finance.setName("Finance");
    finance.setDescription("Equipe finance");
    entityManager.persist(finance);
    entityManager.flush();
    entityManager.clear();

    departementRepository.deleteById(savedId);
    departementRepository.flush();
    entityManager.clear();

    assertThat(departementRepository.findAll())
        .extracting(Departement::getName)
        .containsExactly("Finance");
  }
}
