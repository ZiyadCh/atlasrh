package ma.atlasrh.atlasrh.leave.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.dto.GetLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;

class LeaveRequestMapperTest {

  private final LeaveRequestMapper mapper = Mappers.getMapper(LeaveRequestMapper.class);

  @Test
  void toEntity_shouldMapFields() {
    CreateLeaveRequest dto = new CreateLeaveRequest(LocalDate.of(2025, 1, 5),
        LocalDate.of(2025, 1, 7), "Vacances", 1L);

    LeaveRequest result = mapper.toEntity(dto);

    assertThat(result.getId()).isNull();
    assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 5));
    assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 1, 7));
    assertThat(result.getReason()).isEqualTo("Vacances");
    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.EN_ATTENTE);
    assertThat(result.getEmployee()).isNull();
    assertThat(result.getLeaveType()).isNull();
    assertThat(result.getJourTravail()).isZero();
    assertThat(result.getManagerComment()).isNull();
  }

  @Test
  void toDto_shouldMapFieldsAndEmployeeId() {
    Employee employee = new Employee();
    employee.setId(2L);

    LeaveRequest request = new LeaveRequest();
    request.setId(1L);
    request.setStartDate(LocalDate.of(2025, 1, 5));
    request.setEndDate(LocalDate.of(2025, 1, 7));
    request.setJourTravail(3);
    request.setReason("Vacances");
    request.setStatus(LeaveRequestStatus.EN_ATTENTE);
    request.setEmployee(employee);

    GetLeaveRequestDTO result = mapper.toDto(request);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.startDate()).isEqualTo(LocalDate.of(2025, 1, 5));
    assertThat(result.endDate()).isEqualTo(LocalDate.of(2025, 1, 7));
    assertThat(result.jourTravail()).isEqualTo(3);
    assertThat(result.reason()).isEqualTo("Vacances");
    assertThat(result.status()).isEqualTo(LeaveRequestStatus.EN_ATTENTE);
    assertThat(result.employee()).isEqualTo(2L);
  }
}
