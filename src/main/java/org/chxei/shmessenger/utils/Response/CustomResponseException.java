package org.chxei.shmessenger.utils.Response;

public class CustomResponseException extends Exception {
    private final CustomResponseEntity entity;

    public CustomResponseException(CustomResponseEntity customResponseEntity) {
        this.entity = customResponseEntity;
    }

    public CustomResponseEntity getEntity() {
        return entity;
    }
}
