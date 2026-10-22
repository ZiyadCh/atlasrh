package ma.atlasrh.atlasrh.department.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.atlasrh.atlasrh.department.entity.Departement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * DepartementController
 */
@RestController
@RequestMapping("/api/departement")
public class DepartementController {
  @PostMapping()
  public ResponseEntity<Void> createDepartement(@RequestBody Departement departement,
      @RequestHeader("user-id") Long id) {
    return null;
  }

}
