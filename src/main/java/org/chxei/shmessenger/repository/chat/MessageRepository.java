package org.chxei.shmessenger.repository.chat;

import org.chxei.shmessenger.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}