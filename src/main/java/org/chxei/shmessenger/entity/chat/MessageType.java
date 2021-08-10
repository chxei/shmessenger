package org.chxei.shmessenger.entity.chat;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity(name = "message_types")
@Table(name = "message_types")
public class MessageType {
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
