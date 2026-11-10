package ma.atlasrh.atlasrh.departement.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DepartementRepositoryTest
 */
@DataJpaTest
public class DepartementRepositoryTest {

  @Autowired
  private DepartementRepository departementRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void createDepartementTest() {
    Departement departement = new Departement();
    departement.setName("testName");
    departement.setDescription("testDescription");

    Departement saved = departementRepository.saveAndFlush(departement);
    entityManager.clear();
    assertThat(saved.getId()).isNotNull();
  }

}
