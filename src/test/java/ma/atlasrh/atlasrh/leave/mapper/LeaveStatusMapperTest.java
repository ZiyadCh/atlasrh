package ma.atlasrh.atlasrh.leave.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.atlasrh.atlasrh.leave.dto.ChangeLeaveStatus;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;

class LeaveStatusMapperTest {

  private final LeaveStatusMapper mapper = Mappers.getMapper(LeaveStatusMapper.class);

  @Test
  void toEntity_shouldMapStatusOnly() {
    ChangeLeaveStatus dto = new ChangeLeaveStatus(1L, LeaveRequestStatus.APPROUVEE, "Ok");

    LeaveRequest result = mapper.toEntity(dto);

    assertThat(result.getId()).isNull();
    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.APPROUVEE);
    assertThat(result.getManagerComment()).isNull();
    assertThat(result.getEmployee()).isNull();
    assertThat(result.getLeaveType()).isNull();
    assertThat(result.getJourTravail()).isZero();
    assertThat(result.getStartDate()).isNull();
    assertThat(result.getEndDate()).isNull();
  }
}
