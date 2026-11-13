package ma.atlasrh.atlasrh.contract.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.atlasrh.atlasrh.contract.dto.CreateContractDTO;
import ma.atlasrh.atlasrh.contract.dto.GetContractDTO;
import ma.atlasrh.atlasrh.contract.entity.Contract;
import ma.atlasrh.atlasrh.contract.entity.ContractStatus;
import ma.atlasrh.atlasrh.contract.entity.ContractType;
import ma.atlasrh.atlasrh.employee.entity.Employee;

class ContractMapperTest {

  private final ContractMapper mapper = Mappers.getMapper(ContractMapper.class);

  @Test
  void toEntity_shouldMapFields() {
    CreateContractDTO dto = new CreateContractDTO(ContractType.CDI, LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 12, 31), new BigDecimal("5000.00"), 2L);

    Contract result = mapper.toEntity(dto);

    assertThat(result.getId()).isNull();
    assertThat(result.getContractType()).isEqualTo(ContractType.CDI);
    assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(result.getSalaireBrut()).isEqualByComparingTo("5000.00");
    assertThat(result.getEmployee()).isNull();
  }

  @Test
  void toGetDto_shouldMapFieldsAndEmployeeName() {
    Employee employee = new Employee();
    employee.setId(2L);
    employee.setFirstName("Ahmed");
    employee.setLastName("Alami");

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setContractType(ContractType.CDI);
    contract.setStartDate(LocalDate.of(2025, 1, 1));
    contract.setEndDate(LocalDate.of(2025, 12, 31));
    contract.setSalaireBrut(new BigDecimal("5000.00"));
    contract.setStatus(ContractStatus.ACTIVE);
    contract.setEmployee(employee);

    GetContractDTO result = mapper.toGetDto(contract);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.contractType()).isEqualTo(ContractType.CDI);
    assertThat(result.startDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(result.endDate()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(result.salaireBrut()).isEqualByComparingTo("5000.00");
    assertThat(result.status()).isEqualTo(ContractStatus.ACTIVE);
    assertThat(result.employeeId()).isEqualTo(2L);
    assertThat(result.employee()).isEqualTo("Ahmed Alami");
  }

  @Test
  void toGetDto_shouldHandleNullEmployee() {
    Contract contract = new Contract();
    contract.setId(1L);

    GetContractDTO result = mapper.toGetDto(contract);

    assertThat(result.employeeId()).isNull();
    assertThat(result.employee()).isNull();
  }

  @Test
  void toGetDtoList_shouldMapList() {
    Contract first = new Contract();
    first.setId(1L);

    Contract second = new Contract();
    second.setId(2L);

    List<GetContractDTO> result = mapper.toGetDtoList(List.of(first, second));

    assertThat(result).hasSize(2);
    assertThat(result).extracting(GetContractDTO::id)
        .containsExactly(1L, 2L);
  }
}
