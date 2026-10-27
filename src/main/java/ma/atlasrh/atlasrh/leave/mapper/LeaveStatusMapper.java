package ma.atlasrh.atlasrh.leave.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.atlasrh.atlasrh.leave.dto.ChangeLeaveStatus;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;

/**
 * LeaveStatusMapper
 */
@Mapper(componentModel = "spring")
public interface LeaveStatusMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "managerComment", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "leaveType", ignore = true)
  @Mapping(target = "status", ignore = false)
  @Mapping(target = "jourTravail", ignore = true)
  LeaveRequest toEntity(ChangeLeaveStatus dto);
}
