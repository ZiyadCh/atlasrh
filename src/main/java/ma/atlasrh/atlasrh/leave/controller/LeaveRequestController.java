package ma.atlasrh.atlasrh.leave.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.atlasrh.atlasrh.leave.dto.ChangeLeaveStatus;
import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.dto.GetLeaveRequestDTO;
import ma.atlasrh.atlasrh.leave.service.LeaveRequestService;

/**
 * LeaveRequestController
 */
@RequiredArgsConstructor

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

  private final LeaveRequestService leaveRequestService;

  @PostMapping()
  public ResponseEntity<Void> createLeaveRequest(@Valid @RequestBody CreateLeaveRequest dto,
      @RequestHeader("user-id") Long employeeId) {
    leaveRequestService.create(dto, employeeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping()
  public List<GetLeaveRequestDTO> findAllLeaveRequests(@RequestHeader("user-id") Long employeeId) {
    return leaveRequestService.findAll(employeeId);
  }

  @GetMapping("/{id}")
  public GetLeaveRequestDTO findLeaveRequestById(@PathVariable Long id,
      @RequestHeader("user-id") Long employeeId) {
    return leaveRequestService.findById(id, employeeId);
  }

  @PatchMapping()
  public ResponseEntity<Void> changeLeaveStatus(@Valid @RequestBody ChangeLeaveStatus dto,
      @RequestHeader("user-id") Long employeeId) {
    leaveRequestService.changeStatus(dto, employeeId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
