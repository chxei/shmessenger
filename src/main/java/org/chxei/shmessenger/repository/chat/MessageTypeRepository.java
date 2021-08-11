package org.chxei.shmessenger.repository.chat;

import org.chxei.shmessenger.entity.chat.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageTypeRepository extends JpaRepository<MessageType, Integer> {
    @Query("SELECT e FROM message_types e WHERE e.name=?1")
    List<MessageType> findByName(String s);

}
