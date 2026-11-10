package ma.atlasrh.atlasrh.departement.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.mapper.DepartementMapper;
import ma.atlasrh.atlasrh.departement.repository.DepartementRepository;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;

/**
 * DepartementRepositoryTests
 */
@ExtendWith(MockitoExtension.class)
public class DepartementServiceTests {
  @Mock
  private DepartementRepository departementRepository;
  @Mock
  private DepartementMapper departementMapper;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private DepartementService departementService;

  @Test
  public void departementService_create() {
    CreateDepartementDTO dto = new CreateDepartementDTO("name", "desc");
    Departement departement = new Departement();
    departement.setName("name");
    departement.setDescription("desc");
    when(departementMapper.toEntity(dto)).thenReturn(departement);
    when(departementRepository.save(departement)).thenReturn(departement);
  }

}
