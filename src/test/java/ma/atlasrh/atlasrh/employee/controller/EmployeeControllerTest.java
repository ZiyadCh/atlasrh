package ma.atlasrh.atlasrh.employee.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ma.atlasrh.atlasrh.employee.dto.CreateEmployeeDTO;
import ma.atlasrh.atlasrh.employee.dto.GetEmployeeDTO;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.employee.service.EmployeeService;

/**
 * EmployeeControllerTest
 */
@WebMvcTest(EmployeeController.class)
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private EmployeeService employeeService;

  @Test
  public void shouldReturnPageOfEmployees() throws Exception {
    Long employeeId = 1L;
    GetEmployeeDTO employee = new GetEmployeeDTO(2L, "MAT001", "Ahmed", "Alami",
        "ahmed@atlas.ma", "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", null,
        null, "1 an");
    Page<GetEmployeeDTO> page = new PageImpl<>(List.of(employee));

    when(employeeService.findAll(isNull(), isNull(), any(Pageable.class), eq(employeeId)))
        .thenReturn(page);

    mockMvc.perform(get("/api/employee")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].firstName").value("Ahmed"))
        .andExpect(jsonPath("$.content[0].matricule").value("MAT001"));
  }

  // @Test
  // public void shouldReturnPageOfEmployeesSorted() throws Exception {
  // Long employeeId = 1L;
  // GetEmployeeDTO employee = new GetEmployeeDTO(2L, "MAT001", "Ahmed", "Alami",
  // "ahmed@atlas.ma", "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L,
  // "IT", null,
  // null, "1 an");
  // Page<GetEmployeeDTO> page = new PageImpl<>(List.of(employee));

  // when(employeeService.findAll(isNull(), isNull(), any(Pageable.class),
  // eq(employeeId)))
  // .thenReturn(page);

  // mockMvc.perform(get("/api/employee")
  // .param("sort", "firstName")
  // .header("user-id", employeeId))
  // .andExpect(status().isOk())
  // .andExpect(jsonPath("$.content[0].firstName").value("Ahmed"));
  // }

  // @Test
  // public void shouldReturnPageOfEmployeesWhenSortIsBlank() throws Exception {
  // Long employeeId = 1L;
  // GetEmployeeDTO employee = new GetEmployeeDTO(2L, "MAT001", "Ahmed", "Alami",
  // "ahmed@atlas.ma", "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L,
  // "IT", null,
  // null, "1 an");
  // Page<GetEmployeeDTO> page = new PageImpl<>(List.of(employee));

  // when(employeeService.findAll(isNull(), isNull(), any(Pageable.class),
  // eq(employeeId)))
  // .thenReturn(page);

  // mockMvc.perform(get("/api/employee")
  // .param("sort", "")
  // .header("user-id", employeeId))
  // .andExpect(status().isOk())
  // .andExpect(jsonPath("$.content[0].firstName").value("Ahmed"));
  // }

  @Test
  public void shouldReturnEmployeeById() throws Exception {
    Long employeeId = 1L;
    Long id = 2L;
    GetEmployeeDTO employee = new GetEmployeeDTO(id, "MAT001", "Ahmed", "Alami",
        "ahmed@atlas.ma", "Developpeur", Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", null,
        null, "1 an");

    when(employeeService.findById(id, employeeId)).thenReturn(Optional.of(employee));

    mockMvc.perform(get("/api/employee/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.firstName").value("Ahmed"));
  }

  @Test
  public void shouldReturnEmptyWhenEmployeeNotFound() throws Exception {
    Long employeeId = 1L;
    Long id = 2L;

    when(employeeService.findById(id, employeeId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/employee/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  public void shouldReturnTeamOfManager() throws Exception {
    Long employeeId = 1L;
    Long managerId = 2L;
    List<GetEmployeeDTO> team = List.of(
        new GetEmployeeDTO(3L, "MAT002", "Sara", "Bennani", "sara@atlas.ma", "Developpeur",
            Role.EMPLOYE, LocalDate.of(2025, 1, 1), 3L, "IT", managerId, "Ahmed Alami", "1 an"));

    when(employeeService.getTeam(managerId, employeeId)).thenReturn(team);

    mockMvc.perform(get("/api/employee/manager/{managerId}/team", managerId)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].firstName").value("Sara"));
  }

  @Test
  public void shouldCreateEmployee() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "matricule": "MAT001",
          "firstName": "Ahmed",
          "lastName": "Alami",
          "email": "ahmed@atlas.ma",
          "role": "EMPLOYE"
        }
        """;

    when(employeeService.create(any(CreateEmployeeDTO.class), eq(employeeId)))
        .thenReturn(new Employee());

    mockMvc.perform(post("/api/employee")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated());

    verify(employeeService).create(any(CreateEmployeeDTO.class), eq(employeeId));
  }

  @Test
  public void shouldReturnBadRequestWhenCreatingInvalidEmployee() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "matricule": "MAT001",
          "firstName": "Ahmed",
          "lastName": "Alami",
          "email": "not-an-email",
          "role": "EMPLOYE"
        }
        """;

    mockMvc.perform(post("/api/employee")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldUpdateEmployee() throws Exception {
    Long employeeId = 1L;
    Long id = 2L;
    String body = """
        {
          "matricule": "MAT001",
          "firstName": "Ahmed",
          "lastName": "Alami",
          "email": "ahmed@atlas.ma",
          "role": "MANAGER"
        }
        """;

    GetEmployeeDTO updated = new GetEmployeeDTO(id, "MAT001", "Ahmed", "Alami",
        "ahmed@atlas.ma", "Manager", Role.MANAGER, LocalDate.of(2025, 1, 1), 3L, "IT", null, null,
        "1 an");

    when(employeeService.update(eq(id), any(CreateEmployeeDTO.class), eq(employeeId)))
        .thenReturn(updated);

    mockMvc.perform(put("/api/employee/{id}", id)
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.role").value("MANAGER"));
  }

  @Test
  public void shouldDeleteEmployee() throws Exception {
    Long employeeId = 1L;
    Long id = 2L;

    mockMvc.perform(delete("/api/employee/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isNoContent());

    verify(employeeService).delete(id, employeeId);
  }
}
