package team.router.recycle.web.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class RecycleException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public HttpStatus getHttpStatus() {
        return this.errorCode.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
