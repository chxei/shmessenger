package org.chxei.shmessenger.entity.chat;

import lombok.Getter;
import lombok.Setter;
import org.chxei.shmessenger.entity.user.User;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "participants")
@Table(name = "participants")
public class Participant {
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
}