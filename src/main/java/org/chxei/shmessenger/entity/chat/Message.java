package org.chxei.shmessenger.entity.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.chxei.shmessenger.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Entity(name = "messages")
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Conversation conversation;

    @ManyToOne
    private MessageType messageType;

    @NotNull
    private String content;

    @JsonIgnore
    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @JsonIgnore
    @UpdateTimestamp
    private Timestamp updateTimestamp;

    private boolean isDeleted = false;

    public Message(User user, Conversation conversation, MessageType messageType, String content) {
        this.user = user;
        this.conversation = conversation;
        this.messageType = messageType;
        this.content = content;
    }

    public Message() {

    }
}
