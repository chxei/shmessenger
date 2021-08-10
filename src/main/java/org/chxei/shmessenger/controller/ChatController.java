package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/chat/createConversation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createConversation(@RequestBody Map<String, String> request) {
        int creationUserId = Integer.parseInt(request.get("creationUserId"));
        String conversationName = request.get("conversationName");
        List<Integer> participantIds = Arrays.stream(request.get("participantIds").split(",")).map(Integer::parseInt).collect(Collectors.toList());
        long conversationId = chatService.createConversation(creationUserId, conversationName, participantIds);
        return ResponseEntity.ok(Map.of("conversationId", conversationId));
    }
}