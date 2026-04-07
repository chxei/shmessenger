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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageType that)) return false;
        return name != null && name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : getClass().hashCode();
    }
}
