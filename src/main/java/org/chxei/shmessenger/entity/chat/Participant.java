package org.chxei.shmessenger.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.chxei.shmessenger.entity.user.User;

@Getter
@Setter
@Entity(name = "participants")
@Table(name = "participants")
public final class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Conversation conversation;

    private boolean hasArchived = false;
    private boolean hasMuted = false;
    private boolean hasLeft = false;

    public Participant(User user, Conversation conversation) {
        this.user = user;
        this.conversation = conversation;
    }

    public Participant() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
