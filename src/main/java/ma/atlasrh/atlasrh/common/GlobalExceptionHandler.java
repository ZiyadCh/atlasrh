package ma.atlasrh.atlasrh.common;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import ma.atlasrh.atlasrh.departement.exceptions.DepartementHasEmployeesException;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.leave.exceptions.InvalidDateException;

/**
 * GlobalExceptionHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NoAccessException.class)
  public ResponseEntity<String> noAccessHandler(NoAccessException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidDateException.class)
  public ResponseEntity<String> invalidDateHandler(InvalidDateException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(DepartementHasEmployeesException.class)
  public ResponseEntity<String> departementHasEmployeeHandler(DepartementHasEmployeesException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> illegalArgumentHandler(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> illegalStateHandler(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> entityNotFoundHandler(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> constraintViolationHandler(ConstraintViolationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> httpMessageNotReadableHandler(HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Malformed JSON request");
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> missingParamHandler(MissingServletRequestParameterException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Missing parameter: " + ex.getParameterName());
  }

  @ExceptionHandler(MissingPathVariableException.class)
  public ResponseEntity<String> missingPathVariableHandler(MissingPathVariableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Missing path variable: " + ex.getVariableName());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<String> methodNotSupportedHandler(HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<String> noHandlerFoundHandler(NoHandlerFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint not found: " + ex.getRequestURL());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> validationHandler(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(message.isBlank() ? "Validation failed" : message);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<String> dataIntegrityHandler(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body("Database constraint violation");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> genericHandler(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
  }
}
