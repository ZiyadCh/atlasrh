package ma.atlasrh.atlasrh.departement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import ma.atlasrh.atlasrh.departement.dto.CreateDepartementDTO;
import ma.atlasrh.atlasrh.departement.dto.GetDepartementDTO;
import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.exceptions.DepartementHasEmployeesException;
import ma.atlasrh.atlasrh.departement.service.DepartementService;

/**
 * DepartementControllerTest
 */
@WebMvcTest(DepartementController.class)
@ExtendWith(MockitoExtension.class)
public class DepartementControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DepartementService departementService;

  @Test
  public void shouldReturnListOfDepartement() throws Exception {
    Long employeeId = 1L;
    List<GetDepartementDTO> departements = List.of(
        new GetDepartementDTO("RH", "Ressources humaines"),
        new GetDepartementDTO("IT", "Informatique"));

    when(departementService.findAll(employeeId)).thenReturn(departements);

    mockMvc.perform(get("/api/departement")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nom").value("RH"))
        .andExpect(jsonPath("$[0].description").value("Ressources humaines"))
        .andExpect(jsonPath("$[1].nom").value("IT"))
        .andExpect(jsonPath("$[1].description").value("Informatique"));
  }

  @Test
  public void shouldReturnDepartementById() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;
    GetDepartementDTO departement = new GetDepartementDTO("RH", "Ressources humaines");

    when(departementService.findById(id, employeeId)).thenReturn(Optional.of(departement));

    mockMvc.perform(get("/api/departement/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nom").value("RH"))
        .andExpect(jsonPath("$.description").value("Ressources humaines"));
  }

  @Test
  public void shouldReturnEmptyWhenDepartementNotFound() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;

    when(departementService.findById(id, employeeId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/departement/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  public void shouldCreateDepartement() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "nom": "RH",
          "description": "Ressources humaines"
        }
        """;

    when(departementService.create(any(CreateDepartementDTO.class), eq(employeeId)))
        .thenReturn(new Departement());

    mockMvc.perform(post("/api/departement")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated());

    verify(departementService).create(any(CreateDepartementDTO.class), eq(employeeId));
  }

  @Test
  public void shouldReturnBadRequestWhenCreatingInvalidDepartement() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "nom": "",
          "description": "Ressources humaines"
        }
        """;

    mockMvc.perform(post("/api/departement")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldUpdateDepartement() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;
    String body = """
        {
          "nom": "RH v2",
          "description": "Nouvelle description"
        }
        """;

    Departement updated = new Departement();
    updated.setId(id);
    updated.setName("RH v2");
    updated.setDescription("Nouvelle description");

    when(departementService.update(eq(id), any(CreateDepartementDTO.class), eq(employeeId)))
        .thenReturn(updated);

    mockMvc.perform(put("/api/departement/{id}", id)
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("RH v2"))
        .andExpect(jsonPath("$.description").value("Nouvelle description"));
  }

  @Test
  public void shouldDeleteDepartement() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;

    mockMvc.perform(delete("/api/departement/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isNoContent());

    verify(departementService).delete(id, employeeId);
  }

  @Test
  public void shouldReturnConflictWhenDeletingDepartementWithEmployees() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;

    doThrow(new DepartementHasEmployeesException(id))
        .when(departementService).delete(id, employeeId);

    mockMvc.perform(delete("/api/departement/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isConflict());
  }
}
