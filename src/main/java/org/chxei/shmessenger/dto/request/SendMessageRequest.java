package org.chxei.shmessenger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SendMessageRequest(
        @Positive long conversationId,
        @NotBlank String messageTypeName,
        @NotBlank String content
) {}
