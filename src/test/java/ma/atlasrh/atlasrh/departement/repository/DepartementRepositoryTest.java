package ma.atlasrh.atlasrh.departement.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import ma.atlasrh.atlasrh.departement.entity.Departement;

/**
 * DepartementRepositoryTest
 */
@DataJpaTest
public class DepartementRepositoryTest {

  @Autowired
  private DepartementRepository departementRepository;

  @Test
  public void createDepartementTest() {
    Departement departement = new Departement();
    departement.setName("testName");
    departement.setDescription("testDescription");
  }

}
