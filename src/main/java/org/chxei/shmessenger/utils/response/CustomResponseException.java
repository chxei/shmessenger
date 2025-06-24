package org.chxei.shmessenger.utils.response;

import lombok.Getter;

@Getter
public class CustomResponseException extends Exception {
    private final CustomResponseEntity entity;

    public CustomResponseException(CustomResponseEntity customResponseEntity) {
        this.entity = customResponseEntity;
    }

}
