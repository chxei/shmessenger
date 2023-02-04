package org.chxei.shmessenger.utils.Response;

public enum ResponseCode {
    WRONG_USERNAME_PASSWORD("User with provided username/password combination not found", ResponseType.WARNING),
    WRONG_JWT("Your session has expired, log in again", ResponseType.ACTION),
    WRONG_MESSAGE_TYPE("wrong message type", ResponseType.ERROR),
    USER_WITH_USERNAME_NOT_FOUND("User with given username not found", ResponseType.WARNING),
    USER_UNKNOWN_CONSTRAINT_ERROR("User with given username not found", ResponseType.PIZDEC),
    USER_UNIQUE_CONSTRAINT_VIOLATION("User with these fields is already registered", ResponseType.WARNING),
    CONSTRAINT_UNIQUE_USERS_USERNAME_VIOLATION("User with this username is already registered", ResponseType.WARNING),
    CONSTRAINT_UNIQUE_USERS_EMAIL_VIOLATION("User with this email is already registered", ResponseType.WARNING),
    CONSTRAINT_UNIQUE_USERS_PHONE_VIOLATION("User with this phone is already registered", ResponseType.WARNING);

    private String message;
    private ResponseType responseType;

    ResponseCode(String message, ResponseType responseType) {
        this.message = message;
        this.responseType = responseType;
    }

    ResponseCode(String message) {
        this.message = message;
    }

    ResponseCode(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getMessage() {
        return this.message;
    }

    public ResponseType getResponseType() {
        return this.responseType;
    }
}
