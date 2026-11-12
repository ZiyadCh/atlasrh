package ma.atlasrh.atlasrh.departement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.exceptions.DepartementHasEmployeesException;
import ma.atlasrh.atlasrh.departement.mapper.DepartementMapper;
import ma.atlasrh.atlasrh.departement.repository.DepartementRepository;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;

/**
 * DepartementServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class DepartementServiceTest {

  @Mock
  DepartementRepository departementRepository;
  @Mock
  DepartementMapper departementMapper;
  @Mock
  AuthorizationService authorizationService;
  @Mock
  EmployeeRepository employeeRepository;
  @InjectMocks
  DepartementService departementService;

  @Test
  void create_shouldSaveAndReturnDepartement() {
    Long employeeId = 1L;
    CreateDepartementDTO dto = new CreateDepartementDTO("RH", "Ressources humaines");

    Departement departement = new Departement();
    departement.setName("RH");
    departement.setDescription("Ressources humaines");

    Departement saved = new Departement();
    saved.setId(5L);
    saved.setName("RH");
    saved.setDescription("Ressources humaines");

    when(departementMapper.toEntity(dto)).thenReturn(departement);
    when(departementRepository.save(departement)).thenReturn(saved);

    Departement result = departementService.create(dto, employeeId);

    assertThat(result).isEqualTo(saved);
    assertThat(result.getId()).isEqualTo(5L);
    assertThat(result.getName()).isEqualTo("RH");
    assertThat(result.getDescription()).isEqualTo("Ressources humaines");
    verify(authorizationService).departementRights(employeeId);
    verify(departementRepository).save(departement);
  }

  @Test
  void create_shouldThrowWhenNoAccess() {
    Long employeeId = 1L;
    CreateDepartementDTO dto = new CreateDepartementDTO("RH", "Ressources humaines");

    doThrow(new NoAccessException("No access to departements"))
        .when(authorizationService).departementRights(employeeId);

    assertThatThrownBy(() -> departementService.create(dto, employeeId))
        .isInstanceOf(NoAccessException.class);

    verify(departementMapper, never()).toEntity(any());
    verify(departementRepository, never()).save(any());
  }

  @Test
  void findAll_shouldReturnAllDepartements() {
    Long employeeId = 1L;

    Departement rh = new Departement();
    rh.setId(1L);
    rh.setName("RH");
    rh.setDescription("Ressources humaines");

    Departement it = new Departement();
    it.setId(2L);
    it.setName("IT");
    it.setDescription("Informatique");

    List<Departement> departements = List.of(rh, it);
    List<GetDepartementDTO> dtos = List.of(
        new GetDepartementDTO("RH", "Ressources humaines"),
        new GetDepartementDTO("IT", "Informatique"));

    when(departementRepository.findAll()).thenReturn(departements);
    when(departementMapper.toGetDtoList(departements)).thenReturn(dtos);

    List<GetDepartementDTO> result = departementService.findAll(employeeId);

    assertThat(result).isEqualTo(dtos);
    verify(authorizationService).departementRights(employeeId);
    verify(departementRepository).findAll();
  }

  @Test
  void findById_shouldReturnDepartement() {
    Long employeeId = 1L;
    Long id = 5L;

    Departement departement = new Departement();
    departement.setId(id);
    departement.setName("RH");
    departement.setDescription("Ressources humaines");

    GetDepartementDTO dto = new GetDepartementDTO("RH", "Ressources humaines");

    when(departementRepository.findById(id)).thenReturn(Optional.of(departement));
    when(departementMapper.toGetDto(departement)).thenReturn(dto);

    Optional<GetDepartementDTO> result = departementService.findById(id, employeeId);

    assertThat(result).contains(dto);
    verify(authorizationService).departementRights(employeeId);
    verify(departementRepository).findById(id);
  }

  @Test
  void findById_shouldReturnEmptyWhenNotFound() {
    Long employeeId = 1L;
    Long id = 5L;

    when(departementRepository.findById(id)).thenReturn(Optional.empty());

    Optional<GetDepartementDTO> result = departementService.findById(id, employeeId);

    assertThat(result).isEmpty();
    verify(departementRepository).findById(id);
  }

  @Test
  void update_shouldSaveAndReturnDepartement() {
    Long employeeId = 1L;
    Long id = 5L;
    CreateDepartementDTO dto = new CreateDepartementDTO("RH v2", "Nouvelle description");

    Departement departement = new Departement();
    departement.setName("RH v2");
    departement.setDescription("Nouvelle description");

    Departement saved = new Departement();
    saved.setId(id);
    saved.setName("RH v2");
    saved.setDescription("Nouvelle description");

    when(departementMapper.toEntity(dto)).thenReturn(departement);
    when(departementRepository.save(departement)).thenReturn(saved);

    Departement result = departementService.update(id, dto, employeeId);

    assertThat(result).isEqualTo(saved);
    assertThat(departement.getId()).isEqualTo(id);
    verify(authorizationService).departementRights(employeeId);
    verify(departementRepository).save(departement);
  }

  @Test
  void delete_shouldDeleteDepartement() {
    Long employeeId = 1L;
    Long id = 5L;

    when(employeeRepository.existsByDepartement_Id(id)).thenReturn(false);

    departementService.delete(id, employeeId);

    verify(authorizationService).departementRights(employeeId);
    verify(departementRepository).deleteById(id);
  }

  @Test
  void delete_shouldThrowWhenDepartementHasEmployees() {
    Long employeeId = 1L;
    Long id = 5L;

    when(employeeRepository.existsByDepartement_Id(id)).thenReturn(true);

    assertThatThrownBy(() -> departementService.delete(id, employeeId))
        .isInstanceOf(DepartementHasEmployeesException.class);

    verify(departementRepository, never()).deleteById(id);
  }
}
