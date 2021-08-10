package org.chxei.shmessenger.repository.chat;

import org.chxei.shmessenger.entity.chat.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}