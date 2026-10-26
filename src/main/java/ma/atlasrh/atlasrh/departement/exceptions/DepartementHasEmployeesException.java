package ma.atlasrh.atlasrh.departement.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * DepartementHasEmployeesException
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DepartementHasEmployeesException extends RuntimeException {
  public DepartementHasEmployeesException(Long id) {
    super("Departement " + id + " has employees and cannot be deleted");
  }
}
