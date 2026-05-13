package org.chxei.shmessenger.utils.response;

import lombok.Getter;

@Getter
public class CustomResponseEntity {
    private final ResponseType responseType;
    private final String message;
    private final ResponseCode responseCode;

    public CustomResponseEntity(ResponseType messageType, String message) {
        this.message = message;
        this.responseType = messageType;
        this.responseCode = null;
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
}
