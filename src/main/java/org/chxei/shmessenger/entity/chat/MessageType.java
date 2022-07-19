package org.chxei.shmessenger.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "message_types")
@Table(name = "message_types")
public final class MessageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    public MessageType(String name) {
        this.name = name;
    }

    public MessageType() {
    }
}
