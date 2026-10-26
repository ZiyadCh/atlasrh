package ma.atlasrh.atlasrh.employee.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.employee.dto.CreateEmployeeDTO;
import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;
import ma.atlasrh.atlasrh.employee.service.EmployeeService;

/**
 * EmployeeController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
  private final EmployeeService eService;

  @PostMapping()
  public ResponseEntity<Void> createEmployee(@Valid @RequestBody CreateEmployeeDTO cEmployeeDTO,
      @RequestHeader("user-id") Long employeeId) {
    eService.create(cEmployeeDTO, employeeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping()
  public List<GetEmployeeDTO> findAllEmployees(@RequestHeader("user-id") Long employeeId,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Long departementId,
      @SortDefault(sort = "lastName") Sort sort) {
    return eService.findAll(search, departementId, sort, employeeId);
  }

  @GetMapping("/team")
  public List<GetEmployeeDTO> findTeam(@RequestHeader("user-id") Long employeeId) {
    return eService.findTeam(employeeId);
  }

  @GetMapping("/{id}")
  public GetEmployeeDTO findEmployeeById(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    return eService.findById(id, employeeId).orElse(null);
  }

  @PutMapping("/{id}")
  public ResponseEntity<GetEmployeeDTO> updateEmployee(@PathVariable Long id,
      @Valid @RequestBody CreateEmployeeDTO cEmployeeDTO, @RequestHeader("user-id") Long employeeId) {
    return ResponseEntity.status(HttpStatus.OK).body(eService.update(id, cEmployeeDTO, employeeId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    eService.delete(id, employeeId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
