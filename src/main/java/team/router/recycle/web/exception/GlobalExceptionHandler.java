package team.router.recycle.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import team.router.recycle.Response;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Response response;

    public GlobalExceptionHandler(Response response) {
        this.response = response;
    }

    @ExceptionHandler(RecycleException.class)
    protected ResponseEntity<?> handleRecycleException(RecycleException ex) {
        logger.error(ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return response.fail(errorResponse, ex.getMessage(), ex.getHttpStatus());
    }
}
