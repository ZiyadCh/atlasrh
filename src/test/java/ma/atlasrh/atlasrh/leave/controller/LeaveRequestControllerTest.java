package ma.atlasrh.atlasrh.leave.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ma.atlasrh.atlasrh.leave.dto.CancelLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.dto.ChangeLeaveStatus;
import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.dto.GetLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.dto.LeaveBalanceDTO;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
import ma.atlasrh.atlasrh.leave.service.LeaveRequestService;

/**
 * LeaveRequestControllerTest
 */
@WebMvcTest(LeaveRequestController.class)
@ExtendWith(MockitoExtension.class)
public class LeaveRequestControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LeaveRequestService leaveRequestService;

  @Test
  public void shouldReturnListOfLeaveRequests() throws Exception {
    Long employeeId = 1L;
    List<GetLeaveRequestDTO> requests = List.of(
        new GetLeaveRequestDTO(1L, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 7), 3, "Vacances",
            LeaveRequestStatus.EN_ATTENTE, 2L));

    when(leaveRequestService.findAll(employeeId)).thenReturn(requests);

    mockMvc.perform(get("/api/leave")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].status").value("EN_ATTENTE"));
  }

  @Test
  public void shouldReturnLeaveRequestById() throws Exception {
    Long employeeId = 1L;
    Long id = 5L;
    GetLeaveRequestDTO request = new GetLeaveRequestDTO(id, LocalDate.of(2026, 1, 5),
        LocalDate.of(2026, 1, 7), 3, "Vacances", LeaveRequestStatus.EN_ATTENTE, 2L);

    when(leaveRequestService.findById(id, employeeId)).thenReturn(request);

    mockMvc.perform(get("/api/leave/{id}", id)
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.reason").value("Vacances"));
  }

  @Test
  public void shouldCreateLeaveRequest() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "startDate": "2030-01-05",
          "endDate": "2030-01-07",
          "reason": "Vacances",
          "leaveTypeId": 1
        }
        """;

    when(leaveRequestService.create(any(CreateLeaveRequest.class), eq(employeeId)))
        .thenReturn(new LeaveRequest());

    mockMvc.perform(post("/api/leave")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated());

    verify(leaveRequestService).create(any(CreateLeaveRequest.class), eq(employeeId));
  }

  @Test
  public void shouldChangeLeaveStatus() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "leaveId": 1,
          "status": "APPROUVEE",
          "managerComment": "Ok"
        }
        """;

    when(leaveRequestService.changeStatus(any(ChangeLeaveStatus.class), eq(employeeId)))
        .thenReturn(new LeaveRequest());

    mockMvc.perform(patch("/api/leave")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk());

    verify(leaveRequestService).changeStatus(any(ChangeLeaveStatus.class), eq(employeeId));
  }

  @Test
  public void shouldCancelLeaveRequest() throws Exception {
    Long employeeId = 1L;
    String body = """
        {
          "leaveId": 1,
          "status": "ANNULEE"
        }
        """;

    when(leaveRequestService.cancelRequest(any(CancelLeaveRequestDTO.class), eq(employeeId)))
        .thenReturn(new LeaveRequest());

    mockMvc.perform(patch("/api/leave/cancel")
        .header("user-id", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk());

    verify(leaveRequestService).cancelRequest(any(CancelLeaveRequestDTO.class), eq(employeeId));
  }

  @Test
  public void shouldReturnTeamPendingLeaveRequests() throws Exception {
    Long employeeId = 1L;
    List<GetLeaveRequestDTO> requests = List.of(
        new GetLeaveRequestDTO(1L, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 7), 3, "Vacances",
            LeaveRequestStatus.EN_ATTENTE, 2L));

    when(leaveRequestService.findTeamPending(employeeId)).thenReturn(requests);

    mockMvc.perform(get("/api/leave/team/pending")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L));
  }

  @Test
  public void shouldReturnMyLeaveRequests() throws Exception {
    Long employeeId = 1L;
    List<GetLeaveRequestDTO> requests = List.of(
        new GetLeaveRequestDTO(1L, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 7), 3, "Vacances",
            LeaveRequestStatus.EN_ATTENTE, employeeId));

    when(leaveRequestService.findMyRequests(eq(employeeId), isNull())).thenReturn(requests);

    mockMvc.perform(get("/api/leave/my")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L));
  }

  @Test
  public void shouldReturnLeaveBalances() throws Exception {
    Long employeeId = 1L;
    List<LeaveBalanceDTO> balances = List.of(
        new LeaveBalanceDTO(1L, "Congé annuel", 20, 5, 15));

    when(leaveRequestService.getBalances(employeeId)).thenReturn(balances);

    mockMvc.perform(get("/api/leave/balance")
        .header("user-id", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].leaveTypeName").value("Congé annuel"))
        .andExpect(jsonPath("$[0].daysRemaining").value(15));
  }
}
