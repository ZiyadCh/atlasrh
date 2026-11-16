package ma.atlasrh.atlasrh.departement.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementWithEmployeesDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.employee.mapper.EmployeeMapper;

class DepartementMapperTest {

  private final DepartementMapper mapper = Mappers.getMapper(DepartementMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(mapper, "employeeMapper",
        Mappers.getMapper(EmployeeMapper.class));
  }

  @Test
  void toEntity_shouldMapFields() {
    CreateDepartementDTO dto = new CreateDepartementDTO("RH", "Ressources humaines");

    Departement result = mapper.toEntity(dto);

    assertThat(result.getId()).isNull();
    assertThat(result.getName()).isEqualTo("RH");
    assertThat(result.getDescription()).isEqualTo("Ressources humaines");
  }

  @Test
  void toGetDto_shouldMapFields() {
    Departement departement = new Departement();
    departement.setId(1L);
    departement.setName("RH");
    departement.setDescription("Ressources humaines");

    GetDepartementDTO result = mapper.toGetDto(departement);

    assertThat(result.nom()).isEqualTo("RH");
    assertThat(result.description()).isEqualTo("Ressources humaines");
  }

  @Test
  void toGetDtoList_shouldMapList() {
    Departement rh = new Departement();
    rh.setName("RH");
    rh.setDescription("Ressources humaines");

    Departement it = new Departement();
    it.setName("IT");
    it.setDescription("Informatique");

    List<GetDepartementDTO> result = mapper.toGetDtoList(List.of(rh, it));

    assertThat(result).hasSize(2);
    assertThat(result).extracting(GetDepartementDTO::nom)
        .containsExactly("RH", "IT");
  }

  @Test
  void toGetWithEmployeesDto_shouldMapFieldsAndEmployees() {
    Employee employee = new Employee();
    employee.setId(2L);
    employee.setMatricule("MAT001");
    employee.setFirstName("Ahmed");
    employee.setLastName("Alami");
    employee.setEmail("ahmed@atlas.ma");
    employee.setPoste("Developpeur");
    employee.setRole(Role.EMPLOYE);
    employee.setHireDate(LocalDate.of(2025, 1, 1));

    Departement departement = new Departement();
    departement.setId(1L);
    departement.setName("RH");
    departement.setDescription("Ressources humaines");
    departement.setEmployees(List.of(employee));

    GetDepartementWithEmployeesDTO result = mapper.toGetWithEmployeesDto(departement);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.nom()).isEqualTo("RH");
    assertThat(result.description()).isEqualTo("Ressources humaines");
    assertThat(result.employees()).hasSize(1);
    assertThat(result.employees().get(0).firstName()).isEqualTo("Ahmed");
  }

  @Test
  void toGetWithEmployeesDto_shouldHandleNullEmployees() {
    Departement departement = new Departement();
    departement.setId(1L);
    departement.setName("RH");

    GetDepartementWithEmployeesDTO result = mapper.toGetWithEmployeesDto(departement);

    assertThat(result.employees()).isNull();
  }

  @Test
  void toGetWithEmployeesDtoList_shouldMapList() {
    Departement rh = new Departement();
    rh.setId(1L);
    rh.setName("RH");

    Departement it = new Departement();
    it.setId(2L);
    it.setName("IT");

    List<GetDepartementWithEmployeesDTO> result = mapper
        .toGetWithEmployeesDtoList(List.of(rh, it));

    assertThat(result).hasSize(2);
    assertThat(result).extracting(GetDepartementWithEmployeesDTO::nom)
        .containsExactly("RH", "IT");
  }

  @Test
  void toEntity_shouldReturnNullWhenDtoIsNull() {
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void toGetDto_shouldReturnNullWhenDepartementIsNull() {
    assertThat(mapper.toGetDto(null)).isNull();
  }

  @Test
  void toGetDtoList_shouldReturnNullWhenListIsNull() {
    assertThat(mapper.toGetDtoList(null)).isNull();
  }

  @Test
  void toGetWithEmployeesDto_shouldReturnNullWhenDepartementIsNull() {
    assertThat(mapper.toGetWithEmployeesDto(null)).isNull();
  }

  @Test
  void toGetWithEmployeesDtoList_shouldReturnNullWhenListIsNull() {
    assertThat(mapper.toGetWithEmployeesDtoList(null)).isNull();
  }
}
