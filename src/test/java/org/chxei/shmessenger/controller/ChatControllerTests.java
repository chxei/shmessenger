package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.service.ChatService;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.CustomResponseException;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@WithMockUser(username = "alice")
class ChatControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void createConversationReturnsConversationId() throws Exception {
        when(chatService.createConversation("alice", "Team", List.of(2, 3))).thenReturn(11L);

        mockMvc.perform(post("/chats/conversations")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationName": "Team",
                                  "participantIds": [2, 3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(11));
    }

    @Test
    void createConversationReturnsServiceErrorBody() throws Exception {
        when(chatService.createConversation("alice", null, List.of(999)))
                .thenThrow(new CustomResponseException(new CustomResponseEntity(ResponseCode.WRONG_PARTICIPANTS)));

        mockMvc.perform(post("/chats/conversations")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participantIds": [999]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("WRONG_PARTICIPANTS"));
    }

    @Test
    void createConversationRejectsEmptyParticipantList() throws Exception {
        mockMvc.perform(post("/chats/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationName": "Team",
                                  "participantIds": []
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).createConversation(any(), any(), any());
    }

    @Test
    void sendMessageReturnsMessageId() throws Exception {
        when(chatService.sendMessage("alice", "text", 5L, "hello")).thenReturn(17L);

        mockMvc.perform(post("/chats/messages")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId": 5,
                                  "messageTypeName": "text",
                                  "content": "hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(17));
    }

    @Test
    void sendMessageReturnsServiceErrorBody() throws Exception {
        when(chatService.sendMessage("alice", "text", 5L, "hello"))
                .thenThrow(new CustomResponseException(new CustomResponseEntity(ResponseCode.WRONG_CHAT_FOR_USER)));

        mockMvc.perform(post("/chats/messages")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId": 5,
                                  "messageTypeName": "text",
                                  "content": "hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("WRONG_CHAT_FOR_USER"));
    }

    @Test
    void sendMessageRejectsInvalidBody() throws Exception {
        mockMvc.perform(post("/chats/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId": 0,
                                  "messageTypeName": "",
                                  "content": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).sendMessage(any(), any(), anyLong(), any());
    }

    private TestingAuthenticationToken authentication() {
        return new TestingAuthenticationToken("alice", "password", "app");
    }
}
