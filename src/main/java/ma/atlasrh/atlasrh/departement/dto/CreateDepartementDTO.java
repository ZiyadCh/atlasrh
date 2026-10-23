package ma.atlasrh.atlasrh.departement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartementDTO(
    @NotBlank(message = "Le nom du département est obligatoire") @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères") String nom,
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères") String description) {
}
