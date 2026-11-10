package ma.atlasrh.atlasrh.mail;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * MailController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class MailController {

  private final MailService mailService;

  @PostMapping("/send")
  public ResponseEntity<String> sendLeaveAcceptedEmail(@Valid @RequestBody SendMailRequest request) {
    mailService.sendLeaveAccepted(request.employeeId());
    return ResponseEntity.ok("Email sent successfully");
  }
}
