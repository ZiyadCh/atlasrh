package ma.atlasrh.atlasrh.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import ma.atlasrh.atlasrh.contract.entity.ContractType;

/**
 * GetContractDTO
 */
public record GetContractDTO(ContractType contractType, LocalDate startDate, LocalDate endDate, BigDecimal salaireBrut,
    String employee) {
}
