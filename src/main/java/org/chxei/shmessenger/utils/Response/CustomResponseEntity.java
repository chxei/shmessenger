package org.chxei.shmessenger.utils.Response;

public class CustomResponseEntity {
    private final ResponseType responseType;
    private final String message;
    private final ResponseCode responseCode;

    public CustomResponseEntity(ResponseType messageType, String message) {
        this.message = message;
        this.responseType = messageType;
        this.responseCode = null;
    }

    public CustomResponseEntity(ResponseType responseType, ResponseCode responseCode, String message) {
        this.responseType = responseType;
        this.responseCode = responseCode;
        this.message = message;
    }

    public CustomResponseEntity(ResponseType messageType, ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.message = responseCode.toString();
        this.responseType = messageType;
    }

    public CustomResponseEntity(ResponseCode responseCode, String message) {
        this.message = message;
        this.responseCode = responseCode;
        this.responseType = this.responseCode.getResponseType();
    }

    public CustomResponseEntity(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.responseType = responseCode.getResponseType();
        this.message = responseCode.getMessage();
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }
}

