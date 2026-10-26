package ma.atlasrh.atlasrh.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;

/**
 * GlobalExceptionHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NoAccessException.class)
  public ResponseEntity<String> noAccessHandler(NoAccessException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

}
