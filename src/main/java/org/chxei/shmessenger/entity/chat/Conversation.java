package org.chxei.shmessenger.entity.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.chxei.shmessenger.entity.user.User;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "conversations")
@DynamicInsert
@Table(name = "conversations")
public final class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private User creator;

    @JsonIgnore
    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @JsonIgnore
    @UpdateTimestamp
    private Timestamp updateTimestamp;

    private boolean isDeleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Conversation that = (Conversation) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 1062926945;
    }
}
