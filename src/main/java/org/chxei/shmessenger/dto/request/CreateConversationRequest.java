package org.chxei.shmessenger.dto.request;

import java.util.List;

public record CreateConversationRequest(
    String conversationName,
    List<Integer> participantIds
) {}
