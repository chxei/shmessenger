package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.chat.Conversation;
import org.chxei.shmessenger.entity.chat.Message;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.chat.ConversationRepository;
import org.chxei.shmessenger.repository.chat.MessageRepository;
import org.chxei.shmessenger.repository.chat.ParticipantRepository;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.response.CustomResponseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"})
class ChatServiceIntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createConversationPersistsConversationAndParticipants() throws CustomResponseException {
        User creator = saveUser(unique("creator"));
        User participant = saveUser(unique("participant"));

        long conversationId = chatService.createConversation(creator.getUsername(), null, List.of(participant.getId()));

        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();
        assertEquals(creator, conversation.getCreator());
        assertTrue(conversation.getName().contains(creator.getName()));
        assertTrue(participantRepository.findByConversationAndUser(conversation, creator).isPresent());
        assertTrue(participantRepository.findByConversationAndUser(conversation, participant).isPresent());
    }

    @Test
    void sendMessagePersistsMessageForParticipant() throws CustomResponseException {
        User creator = saveUser(unique("creator"));
        User participant = saveUser(unique("participant"));
        long conversationId = chatService.createConversation(creator.getUsername(), "Service chat", List.of(participant.getId()));

        long messageId = chatService.sendMessage(creator.getUsername(), "COMPOSED", conversationId, "hello");

        Message message = messageRepository.findById(messageId).orElseThrow();
        assertEquals(creator, message.getUser());
        assertEquals(conversationId, message.getConversation().getId());
        assertEquals("COMPOSED", message.getMessageType().getName());
        assertEquals("hello", message.getContent());
    }

    @Test
    void createConversationRejectsMissingParticipants() {
        User creator = saveUser(unique("creator"));

        assertThrows(CustomResponseException.class, () -> chatService.createConversation(creator.getUsername(), null, List.of(999_999)));
    }

    @Test
    void sendMessageRejectsUserOutsideConversation() throws CustomResponseException {
        User creator = saveUser(unique("creator"));
        User participant = saveUser(unique("participant"));
        User outsider = saveUser(unique("outsider"));
        long conversationId = chatService.createConversation(creator.getUsername(), "Service chat", List.of(participant.getId()));

        assertThrows(CustomResponseException.class, () -> chatService.sendMessage(outsider.getUsername(), "COMPOSED", conversationId, "hello"));
    }

    private User saveUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setPhone(username);
        user.setPassword(passwordEncoder.encode("password"));
        return userRepository.save(user);
    }

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
