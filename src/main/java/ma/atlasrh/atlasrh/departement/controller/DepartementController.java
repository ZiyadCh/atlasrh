package ma.atlasrh.atlasrh.departement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
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

}
