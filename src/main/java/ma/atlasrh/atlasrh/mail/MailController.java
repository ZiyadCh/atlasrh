package ma.atlasrh.atlasrh.mail;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MailController
 */
@RestController
@RequestMapping("/api/email")
public class MailController {

  @PostMapping("/send")
  public String postMethodName(@RequestBody String entity) {

    return entity;
  }

}
