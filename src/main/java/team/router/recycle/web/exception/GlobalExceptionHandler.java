package team.router.recycle.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecycleException.class)
    protected ResponseEntity<?> handleRecycleException(RecycleException ex) {
        logger.error(ex);
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }
}
