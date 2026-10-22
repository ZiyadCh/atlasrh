package ma.atlasrh.atlasrh.department.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.atlasrh.atlasrh.department.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.department.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.department.entity.Departement;

@Mapper(componentModel = "spring")
public interface DepartementMapper {

  @Mapping(target = "id", ignore = true)
  Departement toEntity(CreateDepartementDTO dto);

  GetDepartementDTO toGetDto(Departement departement);

  List<GetDepartementDTO> toGetDtoList(List<Departement> departements);
}
