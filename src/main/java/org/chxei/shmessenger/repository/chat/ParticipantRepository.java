package org.chxei.shmessenger.repository.chat;

import org.chxei.shmessenger.entity.chat.Conversation;
import org.chxei.shmessenger.entity.chat.Participant;
import org.chxei.shmessenger.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByConversationAndUser(Conversation conversation, User user);
}