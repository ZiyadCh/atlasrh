package ma.atlasrh.atlasrh.employee.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.employee.dto.CreateEmployeeDTO;
import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;

class EmployeeMapperTest {

  private final EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

  @Test
  void toEntity_shouldMapFields() {
    CreateEmployeeDTO dto = new CreateEmployeeDTO("MAT001", "Ahmed", "Alami", "ahmed@atlas.ma",
        "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, 4L);

    Employee result = mapper.toEntity(dto);

    assertThat(result.getId()).isNull();
    assertThat(result.getMatricule()).isEqualTo("MAT001");
    assertThat(result.getFirstName()).isEqualTo("Ahmed");
    assertThat(result.getLastName()).isEqualTo("Alami");
    assertThat(result.getEmail()).isEqualTo("ahmed@atlas.ma");
    assertThat(result.getPoste()).isEqualTo("Developpeur");
    assertThat(result.getRole()).isEqualTo(Role.EMPLOYE);
    assertThat(result.getHireDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(result.getDepartement()).isNull();
    assertThat(result.getManager()).isNull();
  }

  @Test
  void toGetDto_shouldMapFieldsRelationsAndAnciennete() {
    Departement departement = new Departement();
    departement.setId(3L);
    departement.setName("IT");

    Employee manager = new Employee();
    manager.setId(4L);
    manager.setFirstName("Sara");
    manager.setLastName("Bennani");

    Employee employee = new Employee();
    employee.setId(2L);
    employee.setMatricule("MAT001");
    employee.setFirstName("Ahmed");
    employee.setLastName("Alami");
    employee.setEmail("ahmed@atlas.ma");
    employee.setPoste("Developpeur");
    employee.setRole(Role.EMPLOYE);
    employee.setHireDate(LocalDate.now().minusYears(2));
    employee.setDepartement(departement);
    employee.setManager(manager);

    GetEmployeeDTO result = mapper.toGetDto(employee);

    assertThat(result.id()).isEqualTo(2L);
    assertThat(result.matricule()).isEqualTo("MAT001");
    assertThat(result.firstName()).isEqualTo("Ahmed");
    assertThat(result.lastName()).isEqualTo("Alami");
    assertThat(result.email()).isEqualTo("ahmed@atlas.ma");
    assertThat(result.poste()).isEqualTo("Developpeur");
    assertThat(result.role()).isEqualTo(Role.EMPLOYE);
    assertThat(result.departementId()).isEqualTo(3L);
    assertThat(result.departement()).isEqualTo("IT");
    assertThat(result.managerId()).isEqualTo(4L);
    assertThat(result.manager()).isEqualTo("Sara Bennani");
    assertThat(result.anciennete()).isEqualTo("2 ans et 0 mois");
  }

  @Test
  void toGetDto_shouldHandleNullRelationsAndHireDate() {
    Employee employee = new Employee();
    employee.setId(2L);

    GetEmployeeDTO result = mapper.toGetDto(employee);

    assertThat(result.departementId()).isNull();
    assertThat(result.departement()).isNull();
    assertThat(result.managerId()).isNull();
    assertThat(result.manager()).isNull();
    assertThat(result.anciennete()).isNull();
  }

  @Test
  void toGetDtoList_shouldMapList() {
    Employee first = new Employee();
    first.setId(1L);

    Employee second = new Employee();
    second.setId(2L);

    List<GetEmployeeDTO> result = mapper.toGetDtoList(List.of(first, second));

    assertThat(result).hasSize(2);
    assertThat(result).extracting(GetEmployeeDTO::id)
        .containsExactly(1L, 2L);
  }
}
