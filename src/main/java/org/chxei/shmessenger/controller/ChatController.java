package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.dto.request.CreateConversationRequest;
import org.chxei.shmessenger.dto.request.SendMessageRequest;
import org.chxei.shmessenger.service.ChatService;
import org.chxei.shmessenger.utils.response.CustomResponseException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/conversations", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createConversation(Authentication authentication, @RequestBody CreateConversationRequest request) {
        try {
            long conversationId = chatService.createConversation(authentication.getName(), request.conversationName(), request.participantIds());
            return ResponseEntity.ok(Map.of("conversationId", conversationId));
        } catch (CustomResponseException e) {
            return ResponseEntity.ok(e.getEntity());
        }
    }

    @PostMapping(value = "/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> sendMessage(Authentication authentication, @RequestBody SendMessageRequest request) {
        try {
            long messageId = chatService.sendMessage(authentication.getName(), request.messageTypeName(), request.conversationId(), request.content());
            return ResponseEntity.ok(Map.of("messageId", messageId));
        } catch (CustomResponseException e) {
            return ResponseEntity.ok(e.getEntity());
        }
    }
}
