package ma.atlasrh.atlasrh.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ma.atlasrh.atlasrh.departement.entity.Departement;
import ma.atlasrh.atlasrh.departement.repository.DepartementRepository;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.entity.Role;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequest;
import ma.atlasrh.atlasrh.leave.entity.LeaveRequestStatus;
import ma.atlasrh.atlasrh.leave.entity.LeaveType;
import ma.atlasrh.atlasrh.leave.repository.LeaveRequestRepository;
import ma.atlasrh.atlasrh.leave.repository.LeaveTypeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AtlasRhIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private DepartementRepository departementRepository;

  @Autowired
  private LeaveTypeRepository leaveTypeRepository;

  @Autowired
  private LeaveRequestRepository leaveRequestRepository;

  private Employee rh;

  @BeforeEach
  void setUp() {
    rh = new Employee();
    rh.setMatricule("RH-INT-001");
    rh.setFirstName("Rachid");
    rh.setLastName("Rh");
    rh.setEmail("rh.integration@atlasrh.ma");
    rh.setPoste("Responsable RH");
    rh.setRole(Role.RH);
    rh.setHireDate(LocalDate.now().minusYears(2));
    rh = employeeRepository.saveAndFlush(rh);
  }

  @Test
  void shouldCreateEmployeeThenLeaveRequestThenApproveIt() throws Exception {
    Employee manager = createEmployeeThroughApi("MGR-INT-001", "Mina", "Manager",
        "manager.integration@atlasrh.ma", Role.MANAGER, null);
    Employee employee = createEmployeeThroughApi("EMP-INT-001", "Elias", "Employe",
        "employee.integration@atlasrh.ma", Role.EMPLOYE, manager.getId());

    LeaveType leaveType = leaveTypeRepository.findAll().stream()
        .filter(type -> "Annuel".equals(type.getName()))
        .findFirst()
        .orElseThrow();

    LocalDate startDate = LocalDate.now().plusDays(10);
    LocalDate endDate = LocalDate.now().plusDays(12);
    String leaveBody = """
        {
          "startDate": "%s",
          "endDate": "%s",
          "reason": "Integration test",
          "leaveTypeId": %d
        }
        """.formatted(startDate, endDate, leaveType.getId());

    mockMvc.perform(post("/api/leave")
        .header("user-id", employee.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(leaveBody))
        .andExpect(status().isCreated());

    LeaveRequest created = leaveRequestRepository.findByEmployee_Id(employee.getId()).stream()
        .findFirst()
        .orElseThrow();
    assertThat(created.getStatus()).isEqualTo(LeaveRequestStatus.EN_ATTENTE);
    assertThat(created.getJourTravail()).isPositive();

    String decisionBody = """
        {
          "leaveId": %d,
          "status": "APPROUVEE",
          "managerComment": "Approuve par test integration"
        }
        """.formatted(created.getId());

    mockMvc.perform(patch("/api/leave")
        .header("user-id", manager.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(decisionBody))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/leave/{id}", created.getId())
        .header("user-id", manager.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(created.getId()))
        .andExpect(jsonPath("$.status").value("APPROUVEE"));

    LeaveRequest approved = leaveRequestRepository.findById(created.getId()).orElseThrow();
    assertThat(approved.getStatus()).isEqualTo(LeaveRequestStatus.APPROUVEE);
    assertThat(approved.getManagerComment()).isEqualTo("Approuve par test integration");
  }

  @Test
  void shouldRefuseToDeleteDepartmentContainingEmployees() throws Exception {
    String departmentBody = """
        {
          "nom": "Integration Department",
          "description": "Departement cree par test integration"
        }
        """;

    mockMvc.perform(post("/api/departement")
        .header("user-id", rh.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(departmentBody))
        .andExpect(status().isCreated());

    Departement department = departementRepository.findAll().stream()
        .filter(dept -> "Integration Department".equals(dept.getName()))
        .findFirst()
        .orElseThrow();

    createEmployeeThroughApi("EMP-INT-002", "Sara", "Employe", "employee2.integration@atlasrh.ma",
        Role.EMPLOYE, null, department.getId());

    mockMvc.perform(delete("/api/departement/{id}", department.getId())
        .header("user-id", rh.getId()))
        .andExpect(status().isConflict())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("has employees")));

    assertThat(departementRepository.findById(department.getId())).isPresent();
  }

  private Employee createEmployeeThroughApi(String matricule, String firstName, String lastName,
      String email, Role role, Long managerId) throws Exception {
    return createEmployeeThroughApi(matricule, firstName, lastName, email, role, managerId, null);
  }

  private Employee createEmployeeThroughApi(String matricule, String firstName, String lastName,
      String email, Role role, Long managerId, Long departementId) throws Exception {
    String body = """
        {
          "matricule": "%s",
          "firstName": "%s",
          "lastName": "%s",
          "email": "%s",
          "poste": "Developpeur",
          "role": "%s",
          "hireDate": "2024-01-01",
          "departementId": %s,
          "managerId": %s
        }
        """.formatted(matricule, firstName, lastName, email, role,
        departementId == null ? "null" : departementId,
        managerId == null ? "null" : managerId);

    mockMvc.perform(post("/api/employee")
        .header("user-id", rh.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated());

    return employeeRepository.findAll().stream()
        .filter(employee -> matricule.equals(employee.getMatricule()))
        .findFirst()
        .orElseThrow();
  }
}
