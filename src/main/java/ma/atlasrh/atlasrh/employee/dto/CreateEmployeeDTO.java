package ma.atlasrh.atlasrh.employee.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.atlasrh.atlasrh.employee.entity.Role;

public record CreateEmployeeDTO(
    @NotBlank(message = "Le matricule est obligatoire") @Size(min = 2, max = 50, message = "Le matricule doit contenir entre 2 et 50 caractères") String matricule,
    @NotBlank(message = "Le prénom est obligatoire") @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères") String firstName,
    @NotBlank(message = "Le nom est obligatoire") @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères") String lastName,
    @NotBlank(message = "L'email est obligatoire") @Email(message = "L'email doit être valide") @Size(max = 255, message = "L'email ne doit pas dépasser 255 caractères") String email,
    @Size(max = 100, message = "Le poste ne doit pas dépasser 100 caractères") String poste,
    @NotNull(message = "Le rôle est obligatoire") Role role,
    LocalDate hireDate,
    @NotNull(message = "Le département est obligatoire") Long departementId,
    Long managerId) {
}
