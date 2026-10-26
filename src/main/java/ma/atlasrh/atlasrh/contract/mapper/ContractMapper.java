package ma.atlasrh.atlasrh.contract.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ma.atlasrh.atlasrh.contract.dto.CreateContractDTO;
import ma.atlasrh.atlasrh.contract.dto.GetContractDTO;
import ma.atlasrh.atlasrh.contract.entity.Contract;

@Mapper(componentModel = "spring")
public interface ContractMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "status", ignore = true)
  Contract toEntity(CreateContractDTO dto);

  @Mapping(target = "employeeId", source = "employee.id")
  @Mapping(target = "employee", expression = "java(contract.getEmployee() == null ? null : contract.getEmployee().getFirstName() + \" \" + contract.getEmployee().getLastName())")
  GetContractDTO toGetDto(Contract contract);

  List<GetContractDTO> toGetDtoList(List<Contract> contracts);
}
