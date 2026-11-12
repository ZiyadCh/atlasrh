package ma.atlasrh.atlasrh.departement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementWithEmployeesDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.employee.mapper.EmployeeMapper;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface DepartementMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", source = "nom")
  Departement toEntity(CreateDepartementDTO dto);

  @Mapping(target = "nom", source = "name")
  GetDepartementDTO toGetDto(Departement departement);

  List<GetDepartementDTO> toGetDtoList(List<Departement> departements);

  @Mapping(target = "nom", source = "name")
  GetDepartementWithEmployeesDTO toGetWithEmployeesDto(Departement departement);

  List<GetDepartementWithEmployeesDTO> toGetWithEmployeesDtoList(List<Departement> departements);
}
