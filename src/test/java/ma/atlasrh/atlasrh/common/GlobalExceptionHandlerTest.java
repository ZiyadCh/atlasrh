package ma.atlasrh.atlasrh.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import ma.atlasrh.atlasrh.departement.exceptions.DepartementHasEmployeesException;
import ma.atlasrh.atlasrh.employee.exceptions.NoAccessException;
import ma.atlasrh.atlasrh.leave.exceptions.InvalidDateException;

/**
 * Unit tests for {@link GlobalExceptionHandler}: every handler must return the documented HTTP status and body.
 */
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void noAccessHandler_shouldReturnForbidden() {
    ResponseEntity<String> response = handler.noAccessHandler(new NoAccessException("denied"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isEqualTo("denied");
  }

  @Test
  void invalidDateHandler_shouldReturnBadRequest() {
    ResponseEntity<String> response = handler.invalidDateHandler(new InvalidDateException("bad dates"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("bad dates");
  }

  @Test
  void departementHasEmployeeHandler_shouldReturnConflict() {
    ResponseEntity<String> response = handler
        .departementHasEmployeeHandler(new DepartementHasEmployeesException(1L));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).contains("1");
  }

  @Test
  void illegalArgumentHandler_shouldReturnBadRequest() {
    ResponseEntity<String> response = handler.illegalArgumentHandler(new IllegalArgumentException("bad"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("bad");
  }

  @Test
  void illegalStateHandler_shouldReturnNotFound() {
    ResponseEntity<String> response = handler.illegalStateHandler(new IllegalStateException("missing"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo("missing");
  }

  @Test
  void entityNotFoundHandler_shouldReturnNotFound() {
    ResponseEntity<String> response = handler.entityNotFoundHandler(new EntityNotFoundException("missing"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo("missing");
  }

  @Test
  void constraintViolationHandler_shouldReturnBadRequest() {
    ResponseEntity<String> response = handler
        .constraintViolationHandler(new ConstraintViolationException("violation", Set.of()));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("violation");
  }

  @Test
  void httpMessageNotReadableHandler_shouldReturnBadRequest() {
    HttpMessageNotReadableException ex = new HttpMessageNotReadableException("malformed",
        new MockHttpInputMessage(new byte[0]));

    ResponseEntity<String> response = handler.httpMessageNotReadableHandler(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Malformed JSON request");
  }

  @Test
  void missingParamHandler_shouldReturnBadRequestWithParameterName() {
    ResponseEntity<String> response = handler
        .missingParamHandler(new MissingServletRequestParameterException("status", "String"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Missing parameter: status");
  }

  @Test
  void missingPathVariableHandler_shouldReturnBadRequestWithVariableName() throws Exception {
    MissingPathVariableException ex = new MissingPathVariableException("id", sampleParameter());

    ResponseEntity<String> response = handler.missingPathVariableHandler(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Missing path variable: id");
  }

  @Test
  void methodNotSupportedHandler_shouldReturnMethodNotAllowed() {
    ResponseEntity<String> response = handler
        .methodNotSupportedHandler(new HttpRequestMethodNotSupportedException("PATCH"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    assertThat(response.getBody()).contains("PATCH");
  }

  @Test
  void noHandlerFoundHandler_shouldReturnNotFoundWithUrl() {
    ResponseEntity<String> response = handler
        .noHandlerFoundHandler(new NoHandlerFoundException("GET", "/api/unknown", new HttpHeaders()));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo("Endpoint not found: /api/unknown");
  }

  @Test
  void validationHandler_shouldJoinFieldErrors() throws Exception {
    MethodArgumentNotValidException ex = validationException("matricule", "must not be blank");

    ResponseEntity<String> response = handler.validationHandler(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("matricule: must not be blank");
  }

  @Test
  void validationHandler_shouldReturnDefaultMessageWhenNoFieldErrors() throws Exception {
    MethodArgumentNotValidException ex = validationException(null, null);

    ResponseEntity<String> response = handler.validationHandler(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Validation failed");
  }

  @Test
  void dataIntegrityHandler_shouldReturnDefaultMessageWhenCauseMessageIsNull() {
    DataIntegrityViolationException ex = new DataIntegrityViolationException("wrapper",
        new RuntimeException((String) null));

    ResponseEntity<String> response = handler.dataIntegrityHandler(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isEqualTo("Violation de contrainte de base de données");
  }

  @Test
  void dataIntegrityHandler_shouldMapEmployeeEmailDuplicate() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"employees_email_key\""));

    assertThat(response.getBody()).isEqualTo("Un employé avec cet email existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapEmployeeMatriculeDuplicate() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"employees_matricule_key\""));

    assertThat(response.getBody()).isEqualTo("Un employé avec ce matricule existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapDepartementNameDuplicate() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"departements_name_key\""));

    assertThat(response.getBody()).isEqualTo("Un département avec ce nom existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapContractTypeNameDuplicate() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"contract_types_name_key\""));

    assertThat(response.getBody()).isEqualTo("Un type de contrat avec ce nom existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapLeaveTypeNameDuplicate() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"leave_types_name_key\""));

    assertThat(response.getBody()).isEqualTo("Un type de congé avec ce nom existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapLeaveRequestStatusNameDuplicate() {
    ResponseEntity<String> response = handler.dataIntegrityHandler(
        dataIntegrity("duplicate key value violates unique constraint \"leave_request_statuses_name_key\""));

    assertThat(response.getBody()).isEqualTo("Un statut de congé avec ce nom existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapUnknownDuplicate() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"something_else\""));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleEmployeeConstraintWithoutMatricule() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"employees_other_key\""));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleDepartementConstraintWithoutName() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"departements_other_key\""));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleContractTypeConstraintWithoutName() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"contract_types_other_key\""));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleLeaveTypeConstraintWithoutName() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value violates unique constraint \"leave_types_other_key\""));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleLeaveRequestStatusConstraintWithoutName() {
    ResponseEntity<String> response = handler.dataIntegrityHandler(
        dataIntegrity("duplicate key value violates unique constraint \"leave_request_statuses_other_key\""));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleUniqueConstraintKeyword() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("unique constraint \"employees_email_key\""));

    assertThat(response.getBody()).isEqualTo("Un employé avec cet email existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleDuplicateEntryWithoutConstraintName() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate entry 'x' for key 'y'"));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldHandleSingleQuoteOnly() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("duplicate key value \"unterminated"));

    assertThat(response.getBody()).isEqualTo("Une valeur en doublon existe déjà");
  }

  @Test
  void dataIntegrityHandler_shouldMapForeignKeyViolation() {
    ResponseEntity<String> response = handler
        .dataIntegrityHandler(dataIntegrity("violates foreign key constraint"));

    assertThat(response.getBody())
        .isEqualTo("Impossible de modifier cet élément car il est référencé par d'autres données");
  }

  @Test
  void dataIntegrityHandler_shouldMapNotNullViolation() {
    ResponseEntity<String> response = handler.dataIntegrityHandler(dataIntegrity("not-null violation"));

    assertThat(response.getBody()).isEqualTo("Un champ obligatoire est manquant");
  }

  @Test
  void dataIntegrityHandler_shouldMapNullValueViolation() {
    ResponseEntity<String> response = handler.dataIntegrityHandler(dataIntegrity("null value in column"));

    assertThat(response.getBody()).isEqualTo("Un champ obligatoire est manquant");
  }

  @Test
  void dataIntegrityHandler_shouldReturnOriginalMessageWhenUnknown() {
    ResponseEntity<String> response = handler.dataIntegrityHandler(dataIntegrity("something unexpected"));

    assertThat(response.getBody()).isEqualTo("something unexpected");
  }

  @Test
  void genericHandler_shouldReturnInternalServerError() {
    ResponseEntity<String> response = handler.genericHandler(new RuntimeException("boom"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("Internal server error");
  }

  private DataIntegrityViolationException dataIntegrity(String causeMessage) {
    return new DataIntegrityViolationException("wrapper", new RuntimeException(causeMessage));
  }

  private MethodArgumentNotValidException validationException(String field, String message)
      throws Exception {
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dto");
    if (field != null) {
      bindingResult.addError(new FieldError("dto", field, message));
    }
    return new MethodArgumentNotValidException(sampleParameter(), bindingResult);
  }

  private MethodParameter sampleParameter() throws Exception {
    return new MethodParameter(SampleController.class.getDeclaredMethod("handle", String.class), 0);
  }

  private static class SampleController {
    @SuppressWarnings("unused")
    void handle(String value) {
    }
  }
}
