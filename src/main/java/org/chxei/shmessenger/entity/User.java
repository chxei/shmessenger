package org.chxei.shmessenger.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity(name = "users")
@DynamicInsert
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String userName;

    @NotNull
    private String name;

    //@ColumnDefault("now()")
    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @UpdateTimestamp
    private Timestamp updateTimestamp;

    @ManyToOne
    private Country country;

    @Email
    private String email;
    private String phone;
    //@JsonDeserialize(using= OptimizedTimestampDeserializer.class)
    private Timestamp birthDate;
    private String pathToProfilePicture;
    private String pathToBackgroundPicture;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private boolean isActive = true;
    private boolean isVerified = false;

    @ManyToOne
    private Gender gender;

    public User(String userName, String name, String email, Timestamp birthDate, String password, Gender gender) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.password = password;
        this.gender = gender;
    }

    public User() {
    }

//    @PrePersist
//    public void setCreationDateTime() {
//        this.creationTimeStamp = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    public void setChangeDateTime() {
//        this.modifiedTimestamp = LocalDateTime.now();
//    }

    //do not change order, add new values last
    public enum Role {
        USER,
        ADMIN
    }
}

