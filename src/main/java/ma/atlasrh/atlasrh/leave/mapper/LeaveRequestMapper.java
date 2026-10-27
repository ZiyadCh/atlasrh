package ma.atlasrh.atlasrh.leave.mapper;

import java.time.LocalDate;

import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.dto.GetLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "managerComment", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "leaveType", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "jourTravail", ignore = true)
  LeaveRequest toEntity(CreateLeaveRequest dto);

  @Mapping(target = "employeeMatricule", source = "employee.matricule")
  @Mapping(target = "employeeFirstName", source = "employee.firstName")
  @Mapping(target = "employeeLastName", source = "employee.lastName")
  @Mapping(target = "leaveTypeName", source = "leaveType.name")
  GetLeaveRequestDTO toDto(LeaveRequest leaveRequest);
}
