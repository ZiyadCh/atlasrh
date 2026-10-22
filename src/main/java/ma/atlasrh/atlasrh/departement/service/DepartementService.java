package ma.atlasrh.atlasrh.departement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.mapper.DepartementMapper;
import ma.atlasrh.atlasrh.departement.repository.DepartementRepository;
import ma.atlasrh.atlasrh.employee.service.AuthorizationService;

/**
 * DepartementService
 */
@Service
@RequiredArgsConstructor
public class DepartementService {

  private final DepartementRepository departementRepository;
  private final DepartementMapper dMapper;
  private final AuthorizationService authorizationService;

  @Transactional
  public Departement create(CreateDepartementDTO cDepartementDTO, Long employeeId) {
    authorizationService.departementRights(employeeId);
    Departement departement = dMapper.toEntity(cDepartementDTO);
    return departementRepository.save(departement);
  }

  public List<Departement> findAll(Long employeeId) {
    authorizationService.departementRights(employeeId);
    return departementRepository.findAll();
  }

  public Optional<Departement> findById(Long id, Long employeeId) {
    authorizationService.departementRights(employeeId);
    return departementRepository.findById(id);
  }

  public Departement update(Long id, Departement departement, Long employeeId) {
    authorizationService.departementRights(employeeId);
    departement.setId(id);
    return departementRepository.save(departement);
  }

  public void delete(Long id, Long employeeId) {
    authorizationService.departementRights(employeeId);
    departementRepository.deleteById(id);
  }
}
