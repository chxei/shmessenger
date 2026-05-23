package org.chxei.shmessenger.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateConversationRequest(
        String conversationName,
        @NotEmpty List<Integer> participantIds
) {}
