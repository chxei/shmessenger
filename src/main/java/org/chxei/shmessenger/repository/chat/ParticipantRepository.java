package org.chxei.shmessenger.repository.chat;

import org.chxei.shmessenger.entity.chat.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}