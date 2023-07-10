package team.router.recycle.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class RecycleException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public HttpStatus getHttpStatus() {
        return this.errorCode.getHttpStatus();
    }
}
