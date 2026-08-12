package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.chat.Conversation;
import org.chxei.shmessenger.entity.chat.Message;
import org.chxei.shmessenger.entity.chat.MessageType;
import org.chxei.shmessenger.entity.chat.Participant;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.chat.ConversationRepository;
import org.chxei.shmessenger.repository.chat.MessageRepository;
import org.chxei.shmessenger.repository.chat.MessageTypeRepository;
import org.chxei.shmessenger.repository.chat.ParticipantRepository;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.response.CustomResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private MessageTypeRepository messageTypeRepository;
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void generateConversationNameUsesUserNames() {
        assertEquals("alice", chatService.generateConversationName(List.of(userWithId(1, "alice"))));
        assertEquals("alice, bob", chatService.generateConversationName(List.of(userWithId(1, "alice"), userWithId(2, "bob"))));
        assertEquals("alice, bob and 2 other", chatService.generateConversationName(List.of(
                userWithId(1, "alice"),
                userWithId(2, "bob"),
                userWithId(3, "carol"),
                userWithId(4, "dave"))));
    }

    @Test
    void createConversationIncludesCreatorAsParticipant() throws CustomResponseException {
        User creator = userWithId(1, "creator");
        User participant = userWithId(2, "participant");

        when(userRepository.findByUsername("creator")).thenReturn(Optional.of(creator));
        when(userRepository.findById(2)).thenReturn(Optional.of(participant));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conversation = invocation.getArgument(0);
            conversation.setId(10L);
            return conversation;
        });

        long conversationId = chatService.createConversation("creator", null, List.of(2));

        assertEquals(10L, conversationId);

        ArgumentCaptor<List<Participant>> participantsCaptor = ArgumentCaptor.captor();
        verify(participantRepository).saveAll(participantsCaptor.capture());

        List<Participant> savedParticipants = participantsCaptor.getValue();
        assertEquals(2, savedParticipants.size());
        assertEquals(creator, savedParticipants.get(0).getUser());
        assertEquals(participant, savedParticipants.get(1).getUser());
    }

    @Test
    void createConversationUsesProvidedName() throws CustomResponseException {
        User creator = userWithId(1, "creator");
        User participant = userWithId(2, "participant");

        when(userRepository.findByUsername("creator")).thenReturn(Optional.of(creator));
        when(userRepository.findById(2)).thenReturn(Optional.of(participant));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conversation = invocation.getArgument(0);
            conversation.setId(12L);
            return conversation;
        });

        long conversationId = chatService.createConversation("creator", "Project", List.of(2));

        assertEquals(12L, conversationId);
        ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.captor();
        verify(conversationRepository).save(conversationCaptor.capture());
        assertEquals("Project", conversationCaptor.getValue().getName());
        assertEquals(creator, conversationCaptor.getValue().getCreator());
    }

    @Test
    void createConversationRejectsMissingCreator() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.createConversation("missing", null, List.of(2)));

        verify(conversationRepository, never()).save(any());
        verify(participantRepository, never()).saveAll(any());
    }

    @Test
    void createConversationRejectsEmptyParticipantIds() {
        User creator = userWithId(1, "creator");
        when(userRepository.findByUsername("creator")).thenReturn(Optional.of(creator));

        assertThrows(CustomResponseException.class, () -> chatService.createConversation("creator", null, List.of()));

        verify(conversationRepository, never()).save(any());
        verify(participantRepository, never()).saveAll(any());
    }

    @Test
    void createConversationRejectsInvalidParticipantIds() {
        User creator = userWithId(1, "creator");

        when(userRepository.findByUsername("creator")).thenReturn(Optional.of(creator));
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.createConversation("creator", null, List.of(999)));

        verify(conversationRepository, never()).save(any(Conversation.class));
        verify(participantRepository, never()).saveAll(any());
    }

    @Test
    void sendMessageSavesMessage() throws CustomResponseException {
        User sender = userWithId(1, "sender");
        Conversation conversation = new Conversation();
        conversation.setId(5L);
        MessageType messageType = new MessageType("text");

        when(userRepository.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(conversationRepository.findById(5L)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationAndUser(conversation, sender)).thenReturn(Optional.of(new Participant(sender, conversation)));
        when(messageTypeRepository.findByName("text")).thenReturn(Optional.of(messageType));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(33L);
            return message;
        });

        long messageId = chatService.sendMessage("sender", "text", 5L, "hello");

        assertEquals(33L, messageId);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.captor();
        verify(messageRepository).save(messageCaptor.capture());
        assertEquals(sender, messageCaptor.getValue().getUser());
        assertEquals(conversation, messageCaptor.getValue().getConversation());
        assertEquals(messageType, messageCaptor.getValue().getMessageType());
        assertEquals("hello", messageCaptor.getValue().getContent());
    }

    @Test
    void sendMessageRejectsMissingSender() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.sendMessage("missing", "text", 5L, "hello"));

        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessageRejectsMissingConversation() {
        User sender = userWithId(1, "sender");
        when(userRepository.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(conversationRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.sendMessage("sender", "text", 5L, "hello"));

        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessageRejectsSenderOutsideConversation() {
        User sender = userWithId(1, "sender");
        Conversation conversation = new Conversation();
        conversation.setId(5L);

        when(userRepository.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(conversationRepository.findById(5L)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationAndUser(conversation, sender)).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.sendMessage("sender", "text", 5L, "hello"));

        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessageRejectsUnknownMessageType() {
        User sender = userWithId(1, "sender");
        Conversation conversation = new Conversation();
        conversation.setId(5L);

        when(userRepository.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(conversationRepository.findById(5L)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationAndUser(conversation, sender)).thenReturn(Optional.of(new Participant(sender, conversation)));
        when(messageTypeRepository.findByName("text")).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.sendMessage("sender", "text", 5L, "hello"));

        verify(messageRepository, never()).save(any());
    }

    private User userWithId(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setName(username);
        return user;
    }
}
