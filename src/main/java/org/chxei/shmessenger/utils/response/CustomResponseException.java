package org.chxei.shmessenger.utils.response;

public class CustomResponseException extends Exception {
    private final CustomResponseEntity entity;

    public CustomResponseException(CustomResponseEntity customResponseEntity) {
        this.entity = customResponseEntity;
    }

    public CustomResponseEntity getEntity() {
        return entity;
    }
}
