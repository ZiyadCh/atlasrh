package ma.atlasrh.atlasrh.contract.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ma.atlasrh.atlasrh.contract.dto.CreateContractDTO;
import ma.atlasrh.atlasrh.contract.dto.GetContractDTO;
import ma.atlasrh.atlasrh.contract.entity.Contract;
import ma.atlasrh.atlasrh.contract.entity.ContractStatus;
import ma.atlasrh.atlasrh.contract.entity.ContractType;
import ma.atlasrh.atlasrh.contract.service.ContractService;

/**
 * ContractControllerTest
 */
@WebMvcTest(ContractController.class)
@ExtendWith(MockitoExtension.class)
public class ContractControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ContractService contractService;

  @Test
  public void shouldReturnListOfContracts() throws Exception {
    Long employeeId = 1L;
    List<GetContractDTO> contracts = List.of(
        new GetContractDTO(1L, ContractType.CDI, LocalDate.of(2025, 1, 1), null,
            new BigDecimal("5000.00"), ContractStatus.ACTIVE, 2L, "Ahmed Alami"));

    when(contractService.findAll(employeeId)).thenReturn(contracts);

    mockMvc.perform(get("/api/contract")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].contractType").value("CDI"))
        .andExpect(jsonPath("$[0].employee").value("Ahmed Alami"));
  }

  @Test
  public void shouldReturnContractById() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;
    GetContractDTO contract = new GetContractDTO(id, ContractType.CDI, LocalDate.of(2025, 1, 1),
        null, new BigDecimal("5000.00"), ContractStatus.ACTIVE, 2L, "Ahmed Alami");

    when(contractService.findById(id, employeeId)).thenReturn(Optional.of(contract));

    mockMvc.perform(get("/api/contract/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.contractType").value("CDI"));
  }

  @Test
  public void shouldReturnEmptyWhenContractNotFound() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;

    when(contractService.findById(id, employeeId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/contract/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  public void shouldCreateContract() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "contractType": "CDI",
          "startDate": "2025-01-01",
          "salaireBrut": 5000.00,
          "employeeId": 2
        }
        """;

    when(contractService.create(any(CreateContractDTO.class), eq(employeeId)))
        .thenReturn(new Contract());

    mockMvc.perform(post("/api/contract")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated());

    verify(contractService).create(any(CreateContractDTO.class), eq(employeeId));
  }

  @Test
  public void shouldReturnBadRequestWhenCreatingInvalidContract() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "startDate": "2025-01-01"
        }
        """;

    mockMvc.perform(post("/api/contract")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldUpdateContract() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;
    String body = """
        {
          "contractType": "CDD",
          "startDate": "2025-02-01",
          "salaireBrut": 6000.00,
          "employeeId": 2
        }
        """;

    GetContractDTO updated = new GetContractDTO(id, ContractType.CDD, LocalDate.of(2025, 2, 1),
        null, new BigDecimal("6000.00"), ContractStatus.ACTIVE, 2L, "Ahmed Alami");

    when(contractService.update(eq(id), any(CreateContractDTO.class), eq(employeeId)))
        .thenReturn(updated);

    mockMvc.perform(put("/api/contract/{id}", id)
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.contractType").value("CDD"));
  }

  @Test
  public void shouldReturnContractsByEmployee() throws Exception {
    Long userId = 1L;
    Long targetId = 2L;
    List<GetContractDTO> contracts = List.of(
        new GetContractDTO(1L, ContractType.CDI, LocalDate.of(2025, 1, 1), null,
            new BigDecimal("5000.00"), ContractStatus.ACTIVE, targetId, "Ahmed Alami"));

    when(contractService.findHistoryByEmployee(targetId, userId)).thenReturn(contracts);

    mockMvc.perform(get("/api/contract/employee/{employeeId}", targetId)
        .header("user-id", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].employeeId").value(targetId));
  }

  @Test
  public void shouldTerminateContract() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;
    GetContractDTO terminated = new GetContractDTO(id, ContractType.CDI, LocalDate.of(2025, 1, 1),
        LocalDate.now(), new BigDecimal("5000.00"), ContractStatus.TERMINATED, 2L, "Ahmed Alami");

    when(contractService.terminate(id, employeeId)).thenReturn(Optional.of(terminated));

    mockMvc.perform(patch("/api/contract/{id}/terminate", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("TERMINATED"));
  }

  @Test
  public void shouldReturnNotFoundWhenTerminatingUnknownContract() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;

    when(contractService.terminate(id, employeeId)).thenReturn(Optional.empty());

    mockMvc.perform(patch("/api/contract/{id}/terminate", id)
        .header("user-id", employeeId))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldDeleteContract() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;

    mockMvc.perform(delete("/api/contract/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isNoContent());

    verify(contractService).delete(id, employeeId);
  }
}
