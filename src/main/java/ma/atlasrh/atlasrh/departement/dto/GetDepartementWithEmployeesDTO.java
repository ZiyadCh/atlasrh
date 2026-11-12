package ma.atlasrh.atlasrh.departement.dto;

import java.util.List;

import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;

/**
 * GetDepartementWithEmployeesDTO
 */
public record GetDepartementWithEmployeesDTO(Long id, String nom, String description, List<GetEmployeeDTO> employees) {
}
