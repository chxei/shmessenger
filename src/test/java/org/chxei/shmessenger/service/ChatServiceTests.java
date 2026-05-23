package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.chat.Conversation;
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
    void createConversationRejectsInvalidParticipantIds() {
        User creator = userWithId(1, "creator");

        when(userRepository.findByUsername("creator")).thenReturn(Optional.of(creator));
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(CustomResponseException.class, () -> chatService.createConversation("creator", null, List.of(999)));

        verify(conversationRepository, never()).save(any(Conversation.class));
        verify(participantRepository, never()).saveAll(any());
    }

    private User userWithId(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setName(username);
        return user;
    }
}
