package ma.atlasrh.atlasrh.employee.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.atlasrh.atlasrh.employee.dto.CreateEmployeeDTO;
import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;
import ma.atlasrh.atlasrh.employee.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "departement", ignore = true)
  @Mapping(target = "manager", ignore = true)
  @Mapping(target = "contracts", ignore = true)
  @Mapping(target = "leaveRequests", ignore = true)
  Employee toEntity(CreateEmployeeDTO dto);

  @Mapping(target = "departementId", source = "departement.id")
  @Mapping(target = "departement", source = "departement.name")
  @Mapping(target = "managerId", source = "manager.id")
  @Mapping(target = "manager", expression = "java(employee.getManager() == null ? null : employee.getManager().getFirstName() + \" \" + employee.getManager().getLastName())")
  GetEmployeeDTO toGetDto(Employee employee);

  List<GetEmployeeDTO> toGetDtoList(List<Employee> employees);
}
