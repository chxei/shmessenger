package org.chxei.shmessenger.utils.Response;

public enum ResponseCode {
    WRONG_USERNAME_PASSWORD("User with provided username/password combination not found", ResponseType.WARNING),
    WRONG_JWT("Your session has expired, log in again", ResponseType.ACTION),
    USER_WITH_USERNAME_NOT_FOUND("User with given username not found", ResponseType.WARNING);

    private final String message;
    private ResponseType responseType;

    ResponseCode(String message, ResponseType responseType) {
        this.message = message;
        this.responseType = responseType;
    }

    ResponseCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public ResponseType getResponseType() {
        return this.responseType;
    }
}
