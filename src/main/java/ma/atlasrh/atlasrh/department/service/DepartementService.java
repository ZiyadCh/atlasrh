package ma.atlasrh.atlasrh.department.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.department.entity.Departement;
import ma.atlasrh.atlasrh.department.repository.DepartementRepository;

/**
 * DepartementService
 */
@Service
@RequiredArgsConstructor
public class DepartementService {

  private final DepartementRepository departementRepository;

  public Departement create(Departement departement) {
    return departementRepository.save(departement);
  }

  public List<Departement> findAll() {
    return departementRepository.findAll();
  }

  public Optional<Departement> findById(Long id) {
    return departementRepository.findById(id);
  }

  public Departement update(Long id, Departement departement) {
    departement.setId(id);
    return departementRepository.save(departement);
  }

  public void delete(Long id) {
    departementRepository.deleteById(id);
  }
}
