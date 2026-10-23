package ma.atlasrh.atlasrh.departement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.service.DepartementService;

/**
 * DepartementController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/departement")
public class DepartementController {
  private final DepartementService dService;

  @PostMapping()
  public ResponseEntity<Void> createDepartement(@RequestBody CreateDepartementDTO cDepartementDTO,
      @RequestHeader("user-id") Long employeeId) {
    dService.create(cDepartementDTO, employeeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping()
  public List<GetDepartementDTO> findAllDepartements(@RequestHeader("user-id") Long employeeId) {
    return dService.findAll(employeeId);
  }

  @GetMapping("/{id}")
  public GetDepartementDTO findDepartementById(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    return dService.findById(id, employeeId).orElse(null);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Departement> updateDepartement(@PathVariable Long id,
      @RequestBody CreateDepartementDTO cDepartementDTO, @RequestHeader("user-id") Long employeeId) {
    return ResponseEntity.status(HttpStatus.OK).body(dService.update(id, cDepartementDTO, employeeId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartement(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    dService.delete(id, employeeId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
