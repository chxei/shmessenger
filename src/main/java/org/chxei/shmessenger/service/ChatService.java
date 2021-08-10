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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ConversationRepository conversationRepository;
    @Autowired
    ChatService chatService;
    @Autowired
    MessageTypeRepository messageTypeRepository;
    @Autowired
    MessageRepository messageRepository;


    public String generateConversationName(List<User> users) {
        if (users.size() == 1) {
            return users.get(0).getName();
        } else if (users.size() == 2) {
            return users.get(0).getName() + ", " + users.get(1).getName();
        } else {
            return users.get(0).getName() + ", " + users.get(1).getName() + " and " + (users.size() - 2) + " other";
        }
    }

    public long createConversation(int creationUserId, String conversationName, List<Integer> participantIds) {
        User creationUser = userRepository.findById(creationUserId).stream().findFirst().orElse(null);
        Conversation conversation = new Conversation();
        List<Participant> participants = new ArrayList<>();
        List<User> participantUsers = new ArrayList<>();

        for (int participantId : participantIds) {
            User curUser = userRepository.findById(participantId).stream().findFirst().orElse(null);
            participantUsers.add(curUser);
            participants.add(new Participant(curUser, conversation));
        }
        if (conversationName == null || conversationName.isBlank()) {
            conversationName = chatService.generateConversationName(participantUsers);
        }
        conversation.setName(conversationName);
        conversation.setCreator(creationUser);
        conversationRepository.save(conversation);
        participantRepository.saveAll(participants);
        return conversation.getId();
    }

    public long sendMessage(int senderId, String messageTypeName, long conversationId, String content) {
        User creationUser = userRepository.findById(senderId).stream().findFirst().orElse(null);
        Conversation conversation = conversationRepository.findById(conversationId).stream().findFirst().orElse(null);
        MessageType messageType = messageTypeRepository.findByName(messageTypeName).stream().findFirst().orElse(null);
        Message message = new Message(creationUser, conversation, messageType, content);
        return messageRepository.save(message).getId();
    }
}
