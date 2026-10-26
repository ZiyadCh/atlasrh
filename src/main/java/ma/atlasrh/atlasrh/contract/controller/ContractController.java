package ma.atlasrh.atlasrh.contract.controller;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.contract.dto.CreateContractDTO;
import ma.atlasrh.atlasrh.contract.dto.GetContractDTO;
import ma.atlasrh.atlasrh.contract.service.ContractService;

/**
 * ContractController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/contract")
public class ContractController {
  private final ContractService cService;

  @PostMapping()
  public ResponseEntity<Void> createContract(@Valid @RequestBody CreateContractDTO cContractDTO,
      @RequestHeader("user-id") Long employeeId) {
    cService.create(cContractDTO, employeeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping()
  public List<GetContractDTO> findAllContracts(@RequestHeader("user-id") Long employeeId) {
    return cService.findAll(employeeId);
  }

  @GetMapping("/{id}")
  public GetContractDTO findContractById(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    return cService.findById(id, employeeId).orElse(null);
  }

  @PutMapping("/{id}")
  public ResponseEntity<GetContractDTO> updateContract(@PathVariable Long id,
      @Valid @RequestBody CreateContractDTO cContractDTO, @RequestHeader("user-id") Long employeeId) {
    return ResponseEntity.status(HttpStatus.OK).body(cService.update(id, cContractDTO, employeeId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteContract(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    cService.delete(id, employeeId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
