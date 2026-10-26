package ma.atlasrh.atlasrh.contract.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.contract.dto.CreateContractDTO;
import ma.atlasrh.atlasrh.contract.dto.GetContractDTO;
import ma.atlasrh.atlasrh.contract.entity.Contract;
import ma.atlasrh.atlasrh.contract.mapper.ContractMapper;
import ma.atlasrh.atlasrh.contract.repository.ContractRepository;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;

/**
 * ContractService
 */
@Service
@RequiredArgsConstructor
public class ContractService {

  private final ContractRepository contractRepository;
  private final ContractMapper cMapper;
  private final AuthorizationService authorizationService;
  private final EmployeeRepository employeeRepository;

  @Transactional
  public Contract create(CreateContractDTO cContractDTO, Long employeeId) {
    authorizationService.contractRights(employeeId);
    Contract contract = cMapper.toEntity(cContractDTO);
    setEmployee(contract, cContractDTO.employeeId());
    return contractRepository.save(contract);
  }

  public List<GetContractDTO> findAll(Long employeeId) {
    authorizationService.contractRights(employeeId);
    return cMapper.toGetDtoList(contractRepository.findAll());
  }

  public Optional<GetContractDTO> findById(Long id, Long employeeId) {
    authorizationService.contractRights(employeeId);
    return contractRepository.findById(id).map(cMapper::toGetDto);
  }

  @Transactional
  public GetContractDTO update(Long id, CreateContractDTO cContractDTO, Long employeeId) {
    authorizationService.contractRights(employeeId);
    Contract contract = cMapper.toEntity(cContractDTO);
    contract.setId(id);
    setEmployee(contract, cContractDTO.employeeId());
    return cMapper.toGetDto(contractRepository.save(contract));
  }

  @Transactional
  public void delete(Long id, Long employeeId) {
    authorizationService.contractRights(employeeId);
    contractRepository.deleteById(id);
  }

  private void setEmployee(Contract contract, Long id) {
    if (id != null) {
      contract.setEmployee(employeeRepository.findById(id).orElse(null));
    }
  }
}
