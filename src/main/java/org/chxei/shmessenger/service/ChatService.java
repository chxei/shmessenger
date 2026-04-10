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
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.CustomResponseException;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ConversationRepository conversationRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatService(UserRepository userRepository, ParticipantRepository participantRepository, ConversationRepository conversationRepository, MessageTypeRepository messageTypeRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.conversationRepository = conversationRepository;
        this.messageTypeRepository = messageTypeRepository;
        this.messageRepository = messageRepository;
    }

    public String generateConversationName(List<User> users) {
        if (users.size() == 1) {
            return users.getFirst().getName();
        } else if (users.size() == 2) {
            return users.get(0).getName() + ", " + users.get(1).getName();
        } else {
            return users.get(0).getName() + ", " + users.get(1).getName() + " and " + (users.size() - 2) + " other";
        }
    }

    public long createConversation(String creatorUsername, String conversationName, List<Integer> participantIds) throws CustomResponseException {
        User creationUser = userRepository.findByUsername(creatorUsername).orElse(null);
        Conversation conversation = new Conversation();
        List<Participant> participants = new ArrayList<>();
        List<User> participantUsers = new ArrayList<>();

        for (int participantId : participantIds) {
            User curUser = userRepository.findById(participantId).orElse(null);
            if (curUser != null) {
                participantUsers.add(curUser);
                participants.add(new Participant(curUser, conversation));
            }
        }
        if (participantUsers.isEmpty()) {
            throw new CustomResponseException(new CustomResponseEntity(ResponseCode.WRONG_PARTICIPANTS));
        }
        if (conversationName == null || conversationName.isBlank()) {
            conversationName = generateConversationName(participantUsers);
        }
        conversation.setName(conversationName);
        conversation.setCreator(creationUser);
        conversationRepository.save(conversation);
        participantRepository.saveAll(participants);
        return conversation.getId();
    }

    public long sendMessage(String senderUsername, String messageTypeName, long conversationId, String content) throws CustomResponseException {
        User creationUser = userRepository.findByUsername(senderUsername).orElseThrow(() -> new CustomResponseException(new CustomResponseEntity(ResponseCode.USER_WITH_USERNAME_NOT_FOUND)));
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new CustomResponseException(new CustomResponseEntity(ResponseCode.CONVERSATION_NOT_FOUND)));

        // Authorization check: Is the sender a participant of this conversation?
        if (participantRepository.findByConversationAndUser(conversation, creationUser).isEmpty()) {
            throw new CustomResponseException(new CustomResponseEntity(ResponseCode.WRONG_CHAT_FOR_USER));
        }

        MessageType messageType = messageTypeRepository.findByName(messageTypeName).stream().findFirst().orElse(null);
        if (messageType == null) {
            throw new CustomResponseException(new CustomResponseEntity(ResponseCode.WRONG_MESSAGE_TYPE));
        }
        Message message = new Message(creationUser, conversation, messageType, content);
        return messageRepository.save(message).getId();
    }
}
