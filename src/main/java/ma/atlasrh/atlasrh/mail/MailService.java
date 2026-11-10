package ma.atlasrh.atlasrh.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.atlasrh.atlasrh.employee.entity.Employee;
import ma.atlasrh.atlasrh.employee.repository.EmployeeRepository;

/**
 * MailService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private final EmployeeRepository employeeRepository;

  @Value("${spring.mail.username}")
  private String from;

  public void sendLeaveAccepted(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
    sendLeaveAccepted(employee.getEmail());
  }

  public void sendLeaveAccepted(String email) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(email);
    message.setSubject("Leave request accepted");
    message.setText("your leave request was accepted");
    try {
      javaMailSender.send(message);
    } catch (MailException ex) {
      log.warn("Could not send leave acceptance email to {}", email, ex);
    }
  }
}
