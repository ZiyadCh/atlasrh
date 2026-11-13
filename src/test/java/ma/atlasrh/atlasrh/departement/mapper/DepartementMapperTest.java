package ma.atlasrh.atlasrh.departement.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;

class DepartementMapperTest {

  private final DepartementMapper mapper = Mappers.getMapper(DepartementMapper.class);

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
}
