package org.chxei.shmessenger.repository.chat;

import org.chxei.shmessenger.entity.chat.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTypeRepository extends JpaRepository<MessageType, Long> {
    Optional<MessageType> findByName(String name);
}
