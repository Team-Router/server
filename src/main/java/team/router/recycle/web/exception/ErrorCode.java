package team.router.recycle.web.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST),
    INVALID_TOKEN_VALUE(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND),
    STATION_NOT_FOUND(HttpStatus.NOT_FOUND),
    FAVORITE_STATION_NOT_FOUND(HttpStatus.NOT_FOUND),
    FAVORITE_PLACE_NOT_FOUND(HttpStatus.NOT_FOUND),

    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND),

    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT),

    ALREADY_REGISTERED_FAVORITE(HttpStatus.CONFLICT),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}