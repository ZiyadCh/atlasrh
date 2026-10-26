package ma.atlasrh.atlasrh.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import ma.atlasrh.atlasrh.contract.entity.ContractType;

public record CreateContractDTO(
    @NotNull(message = "Le type de contrat est obligatoire") ContractType contractType,
    @NotNull(message = "La date de début est obligatoire") LocalDate startDate,
    LocalDate endDate,
    @NotNull(message = "Le salaire brut est obligatoire") @DecimalMin(value = "0.0", message = "Le salaire brut ne doit pas être négatif") @Digits(integer = 13, fraction = 2, message = "Le salaire brut doit contenir au maximum 13 chiffres et 2 décimales") BigDecimal salaireBrut,
    @NotNull(message = "L'employé est obligatoire") Long employeeId) {
}
