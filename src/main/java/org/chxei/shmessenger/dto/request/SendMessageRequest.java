package org.chxei.shmessenger.dto.request;

public record SendMessageRequest(
        long conversationId,
        String messageTypeName,
        String content
) {}
