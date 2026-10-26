package ma.atlasrh.atlasrh.employee.dto;

import java.time.LocalDate;

import ma.atlasrh.atlasrh.employee.entity.Role;

/**
 * GetEmployeeDTO
 */
public record GetEmployeeDTO(String matricule, String firstName, String lastName, String email, String poste,
    Role role, LocalDate hireDate, String departement) {
}
