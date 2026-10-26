package ma.atlasrh.atlasrh.leave.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.atlasrh.atlasrh.leave.dto.CreateLeaveRequest;
import ma.atlasrh.atlasrh.leave.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;

/**
 * LeaveRequestController
 */
@RequiredArgsConstructor

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

  private final LeaveRequestService leaveRequestService;

  

  @PostMapping()
  public ResponseEntity<Void> createLeaveRequest(@RequestBody CreateLeaveRequest dto,
      @RequestHeader("user-id") Long employeeId) {
    leaveRequestService.create(dto, employeeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
